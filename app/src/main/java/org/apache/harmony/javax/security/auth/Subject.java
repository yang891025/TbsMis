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

package org.apache.harmony.javax.security.auth;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.DomainCombiner;
import java.security.Permission;
import java.security.Principal;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;



/**
 * The central class of the {@code javax.security.auth} package representing an
 * authenticated user or entity (both referred to as "subject"). IT defines also
 * the static methods that allow code to be run, and do modifications according
 * to the subject's permissions.
 * <p>
 * A subject has the following features:
 * <ul>
 * <li>A set of {@code Principal} objects specifying the identities bound to a
 * {@code Subject} that distinguish it.</li>
 * <li>Credentials (public and private) such as certificates, keys, or
 * authentication proofs such as tickets</li>
 * </ul>
 */
public final class Subject implements Serializable {

    private static final long serialVersionUID = -8308522755600156056L;
    
    private static final AuthPermission _AS = new AuthPermission("doAs"); //$NON-NLS-1$

    private static final AuthPermission _AS_PRIVILEGED = new AuthPermission(
            "doAsPrivileged"); //$NON-NLS-1$

    private static final AuthPermission _SUBJECT = new AuthPermission(
            "getSubject"); //$NON-NLS-1$

    private static final AuthPermission _PRINCIPALS = new AuthPermission(
            "modifyPrincipals"); //$NON-NLS-1$

    private static final AuthPermission _PRIVATE_CREDENTIALS = new AuthPermission(
            "modifyPrivateCredentials"); //$NON-NLS-1$

    private static final AuthPermission _PUBLIC_CREDENTIALS = new AuthPermission(
            "modifyPublicCredentials"); //$NON-NLS-1$

    private static final AuthPermission _READ_ONLY = new AuthPermission(
            "setReadOnly"); //$NON-NLS-1$

    private final Set<Principal> principals;

    private boolean readOnly;
    
    // set of private credentials
    private transient Subject.SecureSet<Object> privateCredentials;

    // set of public credentials
    private transient Subject.SecureSet<Object> publicCredentials;

    /**
     * The default constructor initializing the sets of public and private
     * credentials and principals with the empty set.
     */
    public Subject() {
        this.principals = new Subject.SecureSet<Principal>(Subject._PRINCIPALS);
        this.publicCredentials = new Subject.SecureSet<Object>(Subject._PUBLIC_CREDENTIALS);
        this.privateCredentials = new Subject.SecureSet<Object>(Subject._PRIVATE_CREDENTIALS);

        this.readOnly = false;
    }

    /**
     * The constructor for the subject, setting its public and private
     * credentials and principals according to the arguments.
     *
     * @param readOnly
     *            {@code true} if this {@code Subject} is read-only, thus
     *            preventing any modifications to be done.
     * @param subjPrincipals
     *            the set of Principals that are attributed to this {@code
     *            Subject}.
     * @param pubCredentials
     *            the set of public credentials that distinguish this {@code
     *            Subject}.
     * @param privCredentials
     *            the set of private credentials that distinguish this {@code
     *            Subject}.
     */
    public Subject(boolean readOnly, Set<? extends Principal> subjPrincipals,
            Set<?> pubCredentials, Set<?> privCredentials) {

        if (subjPrincipals == null || pubCredentials == null || privCredentials == null) {
            throw new NullPointerException();
        }

        this.principals = new Subject.SecureSet<Principal>(Subject._PRINCIPALS, subjPrincipals);
        this.publicCredentials = new Subject.SecureSet<Object>(Subject._PUBLIC_CREDENTIALS, pubCredentials);
        this.privateCredentials = new Subject.SecureSet<Object>(Subject._PRIVATE_CREDENTIALS, privCredentials);

        this.readOnly = readOnly;
    }

    /**
     * Runs the code defined by {@code action} using the permissions granted to
     * the {@code Subject} itself and to the code as well.
     *
     * @param subject
     *            the distinguished {@code Subject}.
     * @param action
     *            the code to be run.
     * @return the {@code Object} returned when running the {@code action}.
     */
    @SuppressWarnings("unchecked")
    public static Object doAs(Subject subject, PrivilegedAction action) {

        Subject.checkPermission(Subject._AS);

        return Subject.doAs_PrivilegedAction(subject, action, AccessController.getContext());
    }

    /**
     * Run the code defined by {@code action} using the permissions granted to
     * the {@code Subject} and to the code itself, additionally providing a more
     * specific context.
     *
     * @param subject
     *            the distinguished {@code Subject}.
     * @param action
     *            the code to be run.
     * @param context
     *            the specific context in which the {@code action} is invoked.
     *            if {@code null} a new {@link AccessControlContext} is
     *            instantiated.
     * @return the {@code Object} returned when running the {@code action}.
     */
    @SuppressWarnings("unchecked")
    public static Object doAsPrivileged(Subject subject, PrivilegedAction action,
            AccessControlContext context) {

        Subject.checkPermission(Subject._AS_PRIVILEGED);

        if (context == null) {
            return Subject.doAs_PrivilegedAction(subject, action, new AccessControlContext(
                    new ProtectionDomain[0]));
        }
        return Subject.doAs_PrivilegedAction(subject, action, context);
    }

    // instantiates a new context and passes it to AccessController
    @SuppressWarnings("unchecked")
    private static Object doAs_PrivilegedAction(Subject subject, PrivilegedAction action,
            final AccessControlContext context) {

        AccessControlContext newContext;

        final SubjectDomainCombiner combiner;
        if (subject == null) {
            // performance optimization
            // if subject is null there is nothing to combine
            combiner = null;
        } else {
            combiner = new SubjectDomainCombiner(subject);
        }

        PrivilegedAction dccAction = new PrivilegedAction() {
            @Override
			public Object run() {

                return new AccessControlContext(context, combiner);
            }
        };

        newContext = (AccessControlContext) AccessController.doPrivileged(dccAction);

        return AccessController.doPrivileged(action, newContext);
    }

    /**
     * Runs the code defined by {@code action} using the permissions granted to
     * the subject and to the code itself.
     *
     * @param subject
     *            the distinguished {@code Subject}.
     * @param action
     *            the code to be run.
     * @return the {@code Object} returned when running the {@code action}.
     * @throws PrivilegedActionException
     *             if running the {@code action} throws an exception.
     */
    @SuppressWarnings("unchecked")
    public static Object doAs(Subject subject, PrivilegedExceptionAction action)
            throws PrivilegedActionException {

        Subject.checkPermission(Subject._AS);

        return Subject.doAs_PrivilegedExceptionAction(subject, action, AccessController.getContext());
    }

    /**
     * Runs the code defined by {@code action} using the permissions granted to
     * the subject and to the code itself, additionally providing a more
     * specific context.
     *
     * @param subject
     *            the distinguished {@code Subject}.
     * @param action
     *            the code to be run.
     * @param context
     *            the specific context in which the {@code action} is invoked.
     *            if {@code null} a new {@link AccessControlContext} is
     *            instantiated.
     * @return the {@code Object} returned when running the {@code action}.
     * @throws PrivilegedActionException
     *             if running the {@code action} throws an exception.
     */
    @SuppressWarnings("unchecked")
    public static Object doAsPrivileged(Subject subject,
            PrivilegedExceptionAction action, AccessControlContext context)
            throws PrivilegedActionException {

        Subject.checkPermission(Subject._AS_PRIVILEGED);

        if (context == null) {
            return Subject.doAs_PrivilegedExceptionAction(subject, action,
                    new AccessControlContext(new ProtectionDomain[0]));
        }
        return Subject.doAs_PrivilegedExceptionAction(subject, action, context);
    }

    // instantiates a new context and passes it to AccessController
    @SuppressWarnings("unchecked")
    private static Object doAs_PrivilegedExceptionAction(Subject subject,
            PrivilegedExceptionAction action, final AccessControlContext context)
            throws PrivilegedActionException {

        AccessControlContext newContext;

        final SubjectDomainCombiner combiner;
        if (subject == null) {
            // performance optimization
            // if subject is null there is nothing to combine
            combiner = null;
        } else {
            combiner = new SubjectDomainCombiner(subject);
        }

        PrivilegedAction<AccessControlContext> dccAction = new PrivilegedAction<AccessControlContext>() {
            @Override
			public AccessControlContext run() {
                return new AccessControlContext(context, combiner);
            }
        };

        newContext = AccessController.doPrivileged(dccAction);

        return AccessController.doPrivileged(action, newContext);
    }

    /**
     * Checks two Subjects for equality. More specifically if the principals,
     * public and private credentials are equal, equality for two {@code
     * Subjects} is implied.
     *
     * @param obj
     *            the {@code Object} checked for equality with this {@code
     *            Subject}.
     * @return {@code true} if the specified {@code Subject} is equal to this
     *         one.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Subject that = (Subject) obj;

        return this.principals.equals(that.principals)
                && this.publicCredentials.equals(that.publicCredentials)
                && this.privateCredentials.equals(that.privateCredentials);
    }

    /**
     * Returns this {@code Subject}'s {@link Principal}.
     *
     * @return this {@code Subject}'s {@link Principal}.
     */
    public Set<Principal> getPrincipals() {
        return this.principals;
    }


    /**
     * Returns this {@code Subject}'s {@link Principal} which is a subclass of
     * the {@code Class} provided.
     *
     * @param c
     *            the {@code Class} as a criteria which the {@code Principal}
     *            returned must satisfy.
     * @return this {@code Subject}'s {@link Principal}. Modifications to the
     *         returned set of {@code Principal}s do not affect this {@code
     *         Subject}'s set.
     */
    public <T extends Principal> Set<T> getPrincipals(Class<T> c) {
        return ((Subject.SecureSet<Principal>) this.principals).get(c);
    }

    /**
     * Returns the private credentials associated with this {@code Subject}.
     *
     * @return the private credentials associated with this {@code Subject}.
     */
    public Set<Object> getPrivateCredentials() {
        return this.privateCredentials;
    }

    /**
     * Returns this {@code Subject}'s private credentials which are a subclass
     * of the {@code Class} provided.
     *
     * @param c
     *            the {@code Class} as a criteria which the private credentials
     *            returned must satisfy.
     * @return this {@code Subject}'s private credentials. Modifications to the
     *         returned set of credentials do not affect this {@code Subject}'s
     *         credentials.
     */
    public <T> Set<T> getPrivateCredentials(Class<T> c) {
        return this.privateCredentials.get(c);
    }

    /**
     * Returns the public credentials associated with this {@code Subject}.
     *
     * @return the public credentials associated with this {@code Subject}.
     */
    public Set<Object> getPublicCredentials() {
        return this.publicCredentials;
    }


    /**
     * Returns this {@code Subject}'s public credentials which are a subclass of
     * the {@code Class} provided.
     *
     * @param c
     *            the {@code Class} as a criteria which the public credentials
     *            returned must satisfy.
     * @return this {@code Subject}'s public credentials. Modifications to the
     *         returned set of credentials do not affect this {@code Subject}'s
     *         credentials.
     */
    public <T> Set<T> getPublicCredentials(Class<T> c) {
        return this.publicCredentials.get(c);
    }

    /**
     * Returns a hash code of this {@code Subject}.
     *
     * @return a hash code of this {@code Subject}.
     */
    @Override
    public int hashCode() {
        return this.principals.hashCode() + this.privateCredentials.hashCode()
                + this.publicCredentials.hashCode();
    }

    /**
     * Prevents from modifications being done to the credentials and {@link
     * Principal} sets. After setting it to read-only this {@code Subject} can
     * not be made writable again. The destroy method on the credentials still
     * works though.
     */
    public void setReadOnly() {
        Subject.checkPermission(Subject._READ_ONLY);

        this.readOnly = true;
    }

    /**
     * Returns whether this {@code Subject} is read-only or not.
     *
     * @return whether this {@code Subject} is read-only or not.
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * Returns a {@code String} representation of this {@code Subject}.
     *
     * @return a {@code String} representation of this {@code Subject}.
     */
    @Override
    public String toString() {

        StringBuilder buf = new StringBuilder("Subject:\n"); //$NON-NLS-1$

        Iterator<?> it = this.principals.iterator();
        while (it.hasNext()) {
            buf.append("\tPrincipal: "); //$NON-NLS-1$
            buf.append(it.next());
            buf.append('\n');
        }

        it = this.publicCredentials.iterator();
        while (it.hasNext()) {
            buf.append("\tPublic Credential: "); //$NON-NLS-1$
            buf.append(it.next());
            buf.append('\n');
        }

        int offset = buf.length() - 1;
        it = this.privateCredentials.iterator();
        try {
            while (it.hasNext()) {
                buf.append("\tPrivate Credential: "); //$NON-NLS-1$
                buf.append(it.next());
                buf.append('\n');
            }
        } catch (SecurityException e) {
            buf.delete(offset, buf.length());
            buf.append("\tPrivate Credentials: no accessible information\n"); //$NON-NLS-1$
        }
        return buf.toString();
    }

    private void readObject(ObjectInputStream in) throws IOException,
            ClassNotFoundException {

        in.defaultReadObject();

        this.publicCredentials = new Subject.SecureSet<Object>(Subject._PUBLIC_CREDENTIALS);
        this.privateCredentials = new Subject.SecureSet<Object>(Subject._PRIVATE_CREDENTIALS);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    /**
     * Returns the {@code Subject} that was last associated with the {@code
     * context} provided as argument.
     *
     * @param context
     *            the {@code context} that was associated with the
     *            {@code Subject}.
     * @return the {@code Subject} that was last associated with the {@code
     *         context} provided as argument.
     */
    public static Subject getSubject(final AccessControlContext context) {
        Subject.checkPermission(Subject._SUBJECT);
        if (context == null) {
            throw new NullPointerException("auth.09"); //$NON-NLS-1$
        }
        PrivilegedAction<DomainCombiner> action = new PrivilegedAction<DomainCombiner>() {
            @Override
			public DomainCombiner run() {
                return context.getDomainCombiner();
            }
        };
        DomainCombiner combiner = AccessController.doPrivileged(action);

        if (combiner == null || !(combiner instanceof SubjectDomainCombiner)) {
            return null;
        }
        return ((SubjectDomainCombiner) combiner).getSubject();
    }

    // checks passed permission
    private static void checkPermission(Permission p) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(p);
        }
    }

    // FIXME is used only in two places. remove?
    private void checkState() {
        if (this.readOnly) {
            throw new IllegalStateException("auth.0A"); //$NON-NLS-1$
        }
    }

    private final class SecureSet<SST> extends AbstractSet<SST> implements Serializable {

        /**
         * Compatibility issue: see comments for setType variable
         */
        private static final long serialVersionUID = 7911754171111800359L;

        private final LinkedList<SST> elements;

        /*
         * Is used to define a set type for serialization.
         *
         * A type can be principal, priv. or pub. credential set. The spec.
         * doesn't clearly says that priv. and pub. credential sets can be
         * serialized and what classes they are. It is only possible to figure
         * out from writeObject method comments that priv. credential set is
         * serializable and it is an instance of SecureSet class. So pub.
         * credential was implemented by analogy
         *
         * Compatibility issue: the class follows its specified serial form.
         * Also according to the serialization spec. adding new field is a
         * compatible change. So is ok for principal set (because the default
         * value for integer is zero). But priv. or pub. credential set it is
         * not compatible because most probably other implementations resolve
         * this issue in other way
         */
        private int setType;

        // Defines principal set for serialization.
        private static final int SET_Principal = 0;

        // Defines private credential set for serialization.
        private static final int SET_PrivCred = 1;

        // Defines public credential set for serialization.
        private static final int SET_PubCred = 2;

        // permission required to modify set
        private transient AuthPermission permission;

        protected SecureSet(AuthPermission perm) {
            this.permission = perm;
            this.elements = new LinkedList<SST>();
        }

        // creates set from specified collection with specified permission
        // all collection elements are verified before adding
        protected SecureSet(AuthPermission perm, Collection<? extends SST> s) {
            this(perm);

            // Subject's constructor receives a Set, we can trusts if a set is from bootclasspath,
            // and not to check whether it contains duplicates or not
            boolean trust = s.getClass().getClassLoader() == null;

            Iterator<? extends SST> it = s.iterator();
            while (it.hasNext()) {
                SST o = it.next();
                this.verifyElement(o);
                if (trust || !this.elements.contains(o)) {
                    this.elements.add(o);
                }
            }
        }

        // verifies new set element
        private void verifyElement(Object o) {

            if (o == null) {
                throw new NullPointerException();
            }
            if (this.permission == Subject._PRINCIPALS && !Principal.class.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException("auth.0B"); //$NON-NLS-1$
            }
        }

        /*
         * verifies specified element, checks set state, and security permission
         * to modify set before adding new element
         */
        @Override
        public boolean add(SST o) {

            this.verifyElement(o);

            Subject.this.checkState();
            Subject.checkPermission(this.permission);

            if (!this.elements.contains(o)) {
                this.elements.add(o);
                return true;
            }
            return false;
        }

        // returns an instance of SecureIterator
        @Override
        public Iterator<SST> iterator() {

            if (this.permission == Subject._PRIVATE_CREDENTIALS) {
                /*
                 * private credential set requires iterator with additional
                 * security check (PrivateCredentialPermission)
                 */
                return new Subject.SecureSet.SecureIterator(Subject.SecureSet.this.elements.iterator()) {
                    /*
                     * checks permission to access next private credential moves
                     * to the next element even SecurityException was thrown
                     */
                    @Override
                    public SST next() {
                        SST obj = (SST) iterator.next();
                        Subject.checkPermission(new PrivateCredentialPermission(obj
                                .getClass().getName(), Subject.this.principals));
                        return obj;
                    }
                };
            }
            return new Subject.SecureSet.SecureIterator(this.elements.iterator());
        }

        @Override
        public boolean retainAll(Collection<?> c) {

            if (c == null) {
                throw new NullPointerException();
            }
            return super.retainAll(c);
        }

        @Override
        public int size() {
            return this.elements.size();
        }

        /**
         * return set with elements that are instances or subclasses of the
         * specified class
         */
        protected final <E> Set<E> get(final Class<E> c) {

            if (c == null) {
                throw new NullPointerException();
            }

            AbstractSet<E> s = new AbstractSet<E>() {
                private final LinkedList<E> elements = new LinkedList<E>();

                @Override
                public boolean add(E o) {

                    if (!c.isAssignableFrom(o.getClass())) {
                        throw new IllegalArgumentException(
                                "auth.0C " + c.getName()); //$NON-NLS-1$
                    }

                    if (this.elements.contains(o)) {
                        return false;
                    }
                    this.elements.add(o);
                    return true;
                }

                @Override
                public Iterator<E> iterator() {
                    return this.elements.iterator();
                }

                @Override
                public boolean retainAll(Collection<?> c) {

                    if (c == null) {
                        throw new NullPointerException();
                    }
                    return super.retainAll(c);
                }

                @Override
                public int size() {
                    return this.elements.size();
                }
            };

            // FIXME must have permissions for requested priv. credentials
            for (Iterator<SST> it = this.iterator(); it.hasNext();) {
                SST o = it.next();
                if (c.isAssignableFrom(o.getClass())) {
                    s.add(c.cast(o));
                }
            }
            return s;
        }

        private void readObject(ObjectInputStream in) throws IOException,
                ClassNotFoundException {
            in.defaultReadObject();

            switch (this.setType) {
            case Subject.SecureSet.SET_Principal:
                this.permission = Subject._PRINCIPALS;
                break;
            case Subject.SecureSet.SET_PrivCred:
                this.permission = Subject._PRIVATE_CREDENTIALS;
                break;
            case Subject.SecureSet.SET_PubCred:
                this.permission = Subject._PUBLIC_CREDENTIALS;
                break;
            default:
                throw new IllegalArgumentException();
            }

            Iterator<SST> it = this.elements.iterator();
            while (it.hasNext()) {
                this.verifyElement(it.next());
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException {

            if (this.permission == Subject._PRIVATE_CREDENTIALS) {
                // does security check for each private credential
                for (Iterator<SST> it = this.iterator(); it.hasNext();) {
                    it.next();
                }
                this.setType = Subject.SecureSet.SET_PrivCred;
            } else if (this.permission == Subject._PRINCIPALS) {
                this.setType = Subject.SecureSet.SET_Principal;
            } else {
                this.setType = Subject.SecureSet.SET_PubCred;
            }

            out.defaultWriteObject();
        }

        /**
         * Represents iterator for subject's secure set
         */
        private class SecureIterator implements Iterator<SST> {
            protected Iterator<SST> iterator;

            protected SecureIterator(Iterator<SST> iterator) {
                this.iterator = iterator;
            }

            @Override
			public boolean hasNext() {
                return this.iterator.hasNext();
            }

            @Override
			public SST next() {
                return this.iterator.next();
            }

            /**
             * checks set state, and security permission to modify set before
             * removing current element
             */
            @Override
			public void remove() {
                Subject.this.checkState();
                Subject.checkPermission(Subject.SecureSet.this.permission);
                this.iterator.remove();
            }
        }
    }
}
