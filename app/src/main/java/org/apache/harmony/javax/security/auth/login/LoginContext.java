/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.harmony.javax.security.auth.login;

import java.io.IOException;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedExceptionAction;
import java.security.PrivilegedActionException;

import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.apache.harmony.javax.security.auth.Subject;
import org.apache.harmony.javax.security.auth.callback.CallbackHandler;
import org.apache.harmony.javax.security.auth.callback.Callback;
import org.apache.harmony.javax.security.auth.callback.UnsupportedCallbackException;
import org.apache.harmony.javax.security.auth.spi.LoginModule;
import org.apache.harmony.javax.security.auth.AuthPermission;


public class LoginContext {

    private static final String DEFAULT_CALLBACK_HANDLER_PROPERTY = "auth.login.defaultCallbackHandler"; //$NON-NLS-1$

    /*
     * Integer constants which serve as a replacement for the corresponding
     * LoginModuleControlFlag.* constants. These integers are used later as
     * index in the arrays - see loginImpl() and logoutImpl() methods
     */
    private static final int OPTIONAL = 0;

    private static final int REQUIRED = 1;

    private static final int REQUISITE = 2;

    private static final int SUFFICIENT = 3;

    // Subject to be used for this LoginContext's operations
    private Subject subject;

    /*
     * Shows whether the subject was specified by user (true) or was created by
     * this LoginContext itself (false).
     */
    private boolean userProvidedSubject;

    // Shows whether we use installed or user-provided Configuration
    private boolean userProvidedConfig;

    // An user's AccessControlContext, used when user specifies 
    private AccessControlContext userContext;

    /*
     * Either a callback handler passed by the user or a wrapper for the user's
     * specified handler - see init() below.
     */
    private CallbackHandler callbackHandler;

    /*
     * An array which keeps the instantiated and init()-ialized login modules
     * and their states
     */
    private LoginContext.Module[] modules;

    // Stores a shared state
    private Map<String, ?> sharedState;

    // A context class loader used to load [mainly] LoginModules
    private ClassLoader contextClassLoader;

    // Shows overall status - whether this LoginContext was successfully logged
    private boolean loggedIn;

    public LoginContext(String name) throws LoginException {
        this.init(name, null, null, null);
    }

    public LoginContext(String name, CallbackHandler cbHandler) throws LoginException {
        if (cbHandler == null) {
            throw new LoginException("auth.34"); //$NON-NLS-1$
        }
        this.init(name, null, cbHandler, null);
    }

    public LoginContext(String name, Subject subject) throws LoginException {
        if (subject == null) {
            throw new LoginException("auth.03"); //$NON-NLS-1$
        }
        this.init(name, subject, null, null);
    }

    public LoginContext(String name, Subject subject, CallbackHandler cbHandler)
            throws LoginException {
        if (subject == null) {
            throw new LoginException("auth.03"); //$NON-NLS-1$
        }
        if (cbHandler == null) {
            throw new LoginException("auth.34"); //$NON-NLS-1$
        }
        this.init(name, subject, cbHandler, null);
    }

    public LoginContext(String name, Subject subject, CallbackHandler cbHandler,
            Configuration config) throws LoginException {
        this.init(name, subject, cbHandler, config);
    }

    // Does all the machinery needed for the initialization.
    private void init(String name, Subject subject, final CallbackHandler cbHandler,
            Configuration config) throws LoginException {
        this.userProvidedSubject = (this.subject = subject) != null;

        //
        // Set config
        //
        if (name == null) {
            throw new LoginException("auth.00"); //$NON-NLS-1$
        }

        if (config == null) {
            config = Configuration.getAccessibleConfiguration();
        } else {
            this.userProvidedConfig = true;
        }

        SecurityManager sm = System.getSecurityManager();

        if (sm != null && !this.userProvidedConfig) {
            sm.checkPermission(new AuthPermission("createLoginContext." + name));//$NON-NLS-1$
        }

        AppConfigurationEntry[] entries = config.getAppConfigurationEntry(name);
        if (entries == null) {
            if (sm != null && !this.userProvidedConfig) {
                sm.checkPermission(new AuthPermission("createLoginContext.other")); //$NON-NLS-1$
            }
            entries = config.getAppConfigurationEntry("other"); //$NON-NLS-1$
            if (entries == null) {
                throw new LoginException("auth.35 " + name); //$NON-NLS-1$
            }
        }

        this.modules = new LoginContext.Module[entries.length];
        for (int i = 0; i < this.modules.length; i++) {
            this.modules[i] = new LoginContext.Module(entries[i]);
        }
        //
        // Set CallbackHandler and this.contextClassLoader
        //

        /*
         * as some of the operations to be executed (i.e. get*ClassLoader,
         * getProperty, class loading) are security-checked, then combine all of
         * them into a single doPrivileged() call.
         */
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                @Override
				public Void run() throws Exception {
                    // First, set the 'contextClassLoader'
                    LoginContext.this.contextClassLoader = Thread.currentThread().getContextClassLoader();
                    if (LoginContext.this.contextClassLoader == null) {
                        LoginContext.this.contextClassLoader = ClassLoader.getSystemClassLoader();
                    }
                    // then, checks whether the cbHandler is set
                    if (cbHandler == null) {
                        // well, let's try to find it
                        String klassName = Security
                                .getProperty(LoginContext.DEFAULT_CALLBACK_HANDLER_PROPERTY);
                        if (klassName == null || klassName.length() == 0) {
                            return null;
                        }
                        Class<?> klass = Class.forName(klassName, true, LoginContext.this.contextClassLoader);
                        LoginContext.this.callbackHandler = (CallbackHandler) klass.newInstance();
                    } else {
                        LoginContext.this.callbackHandler = cbHandler;
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException ex) {
            Throwable cause = ex.getCause();
            throw (LoginException) new LoginException("auth.36").initCause(cause);//$NON-NLS-1$
        }

        if (this.userProvidedConfig) {
            this.userContext = AccessController.getContext();
        } else if (this.callbackHandler != null) {
            this.userContext = AccessController.getContext();
            this.callbackHandler = new ContextedCallbackHandler(this.callbackHandler);
        }
    }

    public Subject getSubject() {
        if (this.userProvidedSubject || this.loggedIn) {
            return this.subject;
        }
        return null;
    }

    /**
     * Warning: calling the method more than once may result in undefined
     * behaviour if logout() method is not invoked before.
     */
    public void login() throws LoginException {
        PrivilegedExceptionAction<Void> action = new PrivilegedExceptionAction<Void>() {
            @Override
			public Void run() throws LoginException {
                LoginContext.this.loginImpl();
                return null;
            }
        };
        try {
            if (this.userProvidedConfig) {
                AccessController.doPrivileged(action, this.userContext);
            } else {
                AccessController.doPrivileged(action);
            }
        } catch (PrivilegedActionException ex) {
            throw (LoginException) ex.getException();
        }
    }

    /**
     * The real implementation of login() method whose calls are wrapped into
     * appropriate doPrivileged calls in login().
     */
    private void loginImpl() throws LoginException {
        if (this.subject == null) {
            this.subject = new Subject();
        }

        if (this.sharedState == null) {
            this.sharedState = new HashMap<String, Object>();
        }

        // PHASE 1: Calling login()-s
        Throwable firstProblem = null;

        int[] logged = new int[4];
        int[] total = new int[4];

        for (LoginContext.Module module : this.modules) {
            try {
                // if a module fails during Class.forName(), then it breaks overall
                // attempt - see catch() below
                module.create(this.subject, this.callbackHandler, this.sharedState);

                if (module.module.login()) {
                    ++total[module.getFlag()];
                    ++logged[module.getFlag()];
                    if (module.getFlag() == LoginContext.SUFFICIENT) {
                        break;
                    }
                }
            } catch (Throwable ex) {
                if (firstProblem == null) {
                    firstProblem = ex;
                }
                if (module.klass == null) {
                    /*
                     * an exception occurred during class lookup - overall
                     * attempt must fail a little trick: increase the REQUIRED's
                     * number - this will look like a failed REQUIRED module
                     * later, so overall attempt will fail
                     */
                    ++total[LoginContext.REQUIRED];
                    break;
                }
                ++total[module.getFlag()];
                // something happened after the class was loaded
                if (module.getFlag() == LoginContext.REQUISITE) {
                    // ... and no need to walk down anymore
                    break;
                }
            }
        }
        // end of PHASE1,

        // Let's decide whether we have either overall success or a total failure
        boolean fail = true;

        /*
         * Note: 'failed[xxx]!=0' is not enough to check.
         *
         * Use 'logged[xx] != total[xx]' instead. This is because some modules
         * might not be counted as 'failed' if an exception occurred during
         * preload()/Class.forName()-ing. But, such modules still get counted in
         * the total[].
         */

        // if any REQ* module failed - then it's failure
        if (logged[LoginContext.REQUIRED] != total[LoginContext.REQUIRED] || logged[LoginContext.REQUISITE] != total[LoginContext.REQUISITE]) {
            // fail = true;
        } else {
            if (total[LoginContext.REQUIRED] == 0 && total[LoginContext.REQUISITE] == 0) {
                // neither REQUIRED nor REQUISITE was configured.
                // must have at least one SUFFICIENT or OPTIONAL
                if (logged[LoginContext.OPTIONAL] != 0 || logged[LoginContext.SUFFICIENT] != 0) {
                    fail = false;
                }
                //else { fail = true; }
            } else {
                fail = false;
            }
        }

        int commited[] = new int[4];
        // clear it
        total[0] = total[1] = total[2] = total[3] = 0;
        if (!fail) {
            // PHASE 2:

            for (LoginContext.Module module : this.modules) {
                if (module.klass != null) {
                    ++total[module.getFlag()];
                    try {
                        module.module.commit();
                        ++commited[module.getFlag()];
                    } catch (Throwable ex) {
                        if (firstProblem == null) {
                            firstProblem = ex;
                        }
                    }
                }
            }
        }

        // need to decide once again
        fail = true;
        if (commited[LoginContext.REQUIRED] != total[LoginContext.REQUIRED] || commited[LoginContext.REQUISITE] != total[LoginContext.REQUISITE]) {
            //fail = true;
        } else {
            if (total[LoginContext.REQUIRED] == 0 && total[LoginContext.REQUISITE] == 0) {
                /*
                 * neither REQUIRED nor REQUISITE was configured. must have at
                 * least one SUFFICIENT or OPTIONAL
                 */
                if (commited[LoginContext.OPTIONAL] != 0 || commited[LoginContext.SUFFICIENT] != 0) {
                    fail = false;
                } else {
                    //fail = true;
                }
            } else {
                fail = false;
            }
        }

        if (fail) {
            // either login() or commit() failed. aborting...

            for (LoginContext.Module module : this.modules) {
                try {
                    module.module.abort();
                } catch ( /*LoginException*/Throwable ex) {
                    if (firstProblem == null) {
                        firstProblem = ex;
                    }
                }
            }
            if (firstProblem instanceof PrivilegedActionException
                    && firstProblem.getCause() != null) {
                firstProblem = firstProblem.getCause();
            }
            if (firstProblem instanceof LoginException) {
                throw (LoginException) firstProblem;
            }
            throw (LoginException) new LoginException("auth.37").initCause(firstProblem); //$NON-NLS-1$
        }
        this.loggedIn = true;
    }

    public void logout() throws LoginException {
        PrivilegedExceptionAction<Void> action = new PrivilegedExceptionAction<Void>() {
            @Override
			public Void run() throws LoginException {
                LoginContext.this.logoutImpl();
                return null;
            }
        };
        try {
            if (this.userProvidedConfig) {
                AccessController.doPrivileged(action, this.userContext);
            } else {
                AccessController.doPrivileged(action);
            }
        } catch (PrivilegedActionException ex) {
            throw (LoginException) ex.getException();
        }
    }

    /**
     * The real implementation of logout() method whose calls are wrapped into
     * appropriate doPrivileged calls in logout().
     */
    private void logoutImpl() throws LoginException {
        if (this.subject == null) {
            throw new LoginException("auth.38"); //$NON-NLS-1$
        }
        this.loggedIn = false;
        Throwable firstProblem = null;
        int total = 0;
        for (LoginContext.Module module : this.modules) {
            try {
                module.module.logout();
                ++total;
            } catch (Throwable ex) {
                if (firstProblem == null) {
                    firstProblem = ex;
                }
            }
        }
        if (firstProblem != null || total == 0) {
            if (firstProblem instanceof PrivilegedActionException
                    && firstProblem.getCause() != null) {
                firstProblem = firstProblem.getCause();
            }
            if (firstProblem instanceof LoginException) {
                throw (LoginException) firstProblem;
            }
            throw (LoginException) new LoginException("auth.37").initCause(firstProblem); //$NON-NLS-1$
        }
    }

    /**
     * <p>A class that servers as a wrapper for the CallbackHandler when we use
     * installed Configuration, but not a passed one. See API docs on the
     * LoginContext.</p>
     * 
     * <p>Simply invokes the given handler with the given AccessControlContext.</p>
     */
    private class ContextedCallbackHandler implements CallbackHandler {
        private final CallbackHandler hiddenHandlerRef;

        ContextedCallbackHandler(CallbackHandler handler) {
            hiddenHandlerRef = handler;
        }

        @Override
		public void handle(final Callback[] callbacks) throws IOException,
                UnsupportedCallbackException {
            try {
                AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
                    @Override
					public Void run() throws IOException, UnsupportedCallbackException {
                        ContextedCallbackHandler.this.hiddenHandlerRef.handle(callbacks);
                        return null;
                    }
                }, LoginContext.this.userContext);
            } catch (PrivilegedActionException ex) {
                if (ex.getCause() instanceof UnsupportedCallbackException) {
                    throw (UnsupportedCallbackException) ex.getCause();
                }
                throw (IOException) ex.getCause();
            }
        }
    }

    /** 
     * A private class that stores an instantiated LoginModule.
     */
    private final class Module {

        // An initial info about the module to be used
        AppConfigurationEntry entry;

        // A mapping of LoginModuleControlFlag onto a simple int constant
        int flag;

        // The LoginModule itself 
        LoginModule module;

        // A class of the module
        Class<?> klass;

        Module(AppConfigurationEntry entry) {
            this.entry = entry;
            AppConfigurationEntry.LoginModuleControlFlag flg = entry.getControlFlag();
            if (flg == AppConfigurationEntry.LoginModuleControlFlag.OPTIONAL) {
                this.flag = LoginContext.OPTIONAL;
            } else if (flg == AppConfigurationEntry.LoginModuleControlFlag.REQUISITE) {
                this.flag = LoginContext.REQUISITE;
            } else if (flg == AppConfigurationEntry.LoginModuleControlFlag.SUFFICIENT) {
                this.flag = LoginContext.SUFFICIENT;
            } else {
                this.flag = LoginContext.REQUIRED;
                //if(flg!=LoginModuleControlFlag.REQUIRED) throw new Error()
            }
        }

        int getFlag() {
            return this.flag;
        }

        /**
         * Loads class of the LoginModule, instantiates it and then calls
         * initialize().
         */
        void create(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState)
                throws LoginException {
            String klassName = this.entry.getLoginModuleName();
            if (this.klass == null) {
                try {
                    this.klass = Class.forName(klassName, false, LoginContext.this.contextClassLoader);
                } catch (ClassNotFoundException ex) {
                    throw (LoginException) new LoginException(
                            "auth.39 " + klassName).initCause(ex); //$NON-NLS-1$
                }
            }

            if (this.module == null) {
                try {
                    this.module = (LoginModule) this.klass.newInstance();
                } catch (IllegalAccessException ex) {
                    throw (LoginException) new LoginException(
                            "auth.3A " + klassName) //$NON-NLS-1$
                            .initCause(ex);
                } catch (InstantiationException ex) {
                    throw (LoginException) new LoginException(
                            "auth.3A" + klassName) //$NON-NLS-1$
                            .initCause(ex);
                }
                this.module.initialize(subject, callbackHandler, sharedState, this.entry.getOptions());
            }
        }
    }
}
