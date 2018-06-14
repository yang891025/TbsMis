/**
 * $RCSfile$
 * $Revision$
 * $Date$
 *
 * Copyright 2003-2007 Jive Software.
 *
 * All rights reserved. Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.smack.packet;

import org.jivesoftware.smack.util.StringUtils;

import java.util.*;

/**
 * Represents XMPP message packets. A message can be one of several types:
 *
 * <ul>
 *      <li>Message.Type.NORMAL -- (Default) a normal text message used in email like interface.
 *      <li>Message.Type.CHAT -- a typically short text message used in line-by-line chat interfaces.
 *      <li>Message.Type.GROUP_CHAT -- a chat message sent to a groupchat server for group chats.
 *      <li>Message.Type.HEADLINE -- a text message to be displayed in scrolling marquee displays.
 *      <li>Message.Type.ERROR -- indicates a messaging error.
 * </ul>
 *
 * For each message type, different message fields are typically used as follows:
 * <p>
 * <table border="1">
 * <tr><td>&nbsp;</td><td colspan="5"><b>Message type</b></td></tr>
 * <tr><td><i>Field</i></td><td><b>Normal</b></td><td><b>Chat</b></td><td><b>Group Chat</b></td><td><b>Headline</b></td><td><b>XMPPError</b></td></tr>
 * <tr><td><i>subject</i></td> <td>SHOULD</td><td>SHOULD NOT</td><td>SHOULD NOT</td><td>SHOULD NOT</td><td>SHOULD NOT</td></tr>
 * <tr><td><i>thread</i></td>  <td>OPTIONAL</td><td>SHOULD</td><td>OPTIONAL</td><td>OPTIONAL</td><td>SHOULD NOT</td></tr>
 * <tr><td><i>body</i></td>    <td>SHOULD</td><td>SHOULD</td><td>SHOULD</td><td>SHOULD</td><td>SHOULD NOT</td></tr>
 * <tr><td><i>error</i></td>   <td>MUST NOT</td><td>MUST NOT</td><td>MUST NOT</td><td>MUST NOT</td><td>MUST</td></tr>
 * </table>
 *
 * @author Matt Tucker
 */
public class Message extends Packet {

    private Message.Type type = Message.Type.normal;
    private String thread;
    private String language;

    private final Set<Message.Subject> subjects = new HashSet<Message.Subject>();
    private final Set<Message.Body> bodies = new HashSet<Message.Body>();

    /**
     * Creates a new, "normal" message.
     */
    public Message() {
    }

    /**
     * Creates a new "normal" message to the specified recipient.
     *
     * @param to the recipient of the message.
     */
    public Message(String to) {
        this.setTo(to);
    }

    /**
     * Creates a new message of the specified type to a recipient.
     *
     * @param to the user to send the message to.
     * @param type the message type.
     */
    public Message(String to, Message.Type type) {
        this.setTo(to);
        this.type = type;
    }

    /**
     * Returns the type of the message. If no type has been set this method will return {@link
     * Message.Type#normal}.
     *
     * @return the type of the message.
     */
    public Message.Type getType() {
        return this.type;
    }

    /**
     * Sets the type of the message.
     *
     * @param type the type of the message.
     * @throws IllegalArgumentException if null is passed in as the type
     */
    public void setType(Message.Type type) {
        if (type == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        this.type = type;
    }

    /**
     * Returns the default subject of the message, or null if the subject has not been set.
     * The subject is a short description of message contents.
     * <p>
     * The default subject of a message is the subject that corresponds to the message's language.
     * (see {@link #getLanguage()}) or if no language is set to the applications default
     * language (see {@link Packet#getDefaultLanguage()}).
     *
     * @return the subject of the message.
     */
    public String getSubject() {
        return this.getSubject(null);
    }

    /**
     * Returns the subject corresponding to the language. If the language is null, the method result
     * will be the same as {@link #getSubject()}. Null will be returned if the language does not have
     * a corresponding subject.
     *
     * @param language the language of the subject to return.
     * @return the subject related to the passed in language.
     */
    public String getSubject(String language) {
        Message.Subject subject = this.getMessageSubject(language);
        return subject == null ? null : subject.subject;
    }

    private Message.Subject getMessageSubject(String language) {
        language = this.determineLanguage(language);
        for (Message.Subject subject : this.subjects) {
            if (language.equals(subject.language)) {
                return subject;
            }
        }
        return null;
    }

    /**
     * Returns a set of all subjects in this Message, including the default message subject accessible
     * from {@link #getSubject()}.
     *
     * @return a collection of all subjects in this message.
     */
    public Collection<Message.Subject> getSubjects() {
        return Collections.unmodifiableCollection(this.subjects);
    }

    /**
     * Sets the subject of the message. The subject is a short description of
     * message contents.
     *
     * @param subject the subject of the message.
     */
    public void setSubject(String subject) {
        if (subject == null) {
            this.removeSubject(""); // use empty string because #removeSubject(null) is ambiguous
            return;
        }
        this.addSubject(null, subject);
    }

    /**
     * Adds a subject with a corresponding language.
     *
     * @param language the language of the subject being added.
     * @param subject the subject being added to the message.
     * @return the new {@link Message.Subject}
     * @throws NullPointerException if the subject is null, a null pointer exception is thrown
     */
    public Message.Subject addSubject(String language, String subject) {
        language = this.determineLanguage(language);
        Message.Subject messageSubject = new Message.Subject(language, subject);
        this.subjects.add(messageSubject);
        return messageSubject;
    }

    /**
     * Removes the subject with the given language from the message.
     *
     * @param language the language of the subject which is to be removed
     * @return true if a subject was removed and false if it was not.
     */
    public boolean removeSubject(String language) {
        language = this.determineLanguage(language);
        for (Message.Subject subject : this.subjects) {
            if (language.equals(subject.language)) {
                return this.subjects.remove(subject);
            }
        }
        return false;
    }

    /**
     * Removes the subject from the message and returns true if the subject was removed.
     *
     * @param subject the subject being removed from the message.
     * @return true if the subject was successfully removed and false if it was not.
     */
    public boolean removeSubject(Message.Subject subject) {
        return this.subjects.remove(subject);
    }

    /**
     * Returns all the languages being used for the subjects, not including the default subject.
     *
     * @return the languages being used for the subjects.
     */
    public Collection<String> getSubjectLanguages() {
        Message.Subject defaultSubject = this.getMessageSubject(null);
        List<String> languages = new ArrayList<String>();
        for (Message.Subject subject : this.subjects) {
            if (!subject.equals(defaultSubject)) {
                languages.add(subject.language);
            }
        }
        return Collections.unmodifiableCollection(languages);
    }

    /**
     * Returns the default body of the message, or null if the body has not been set. The body
     * is the main message contents.
     * <p>
     * The default body of a message is the body that corresponds to the message's language.
     * (see {@link #getLanguage()}) or if no language is set to the applications default
     * language (see {@link Packet#getDefaultLanguage()}).
     *
     * @return the body of the message.
     */
    public String getBody() {
        return this.getBody(null);
    }

    /**
     * Returns the body corresponding to the language. If the language is null, the method result
     * will be the same as {@link #getBody()}. Null will be returned if the language does not have
     * a corresponding body.
     *
     * @param language the language of the body to return.
     * @return the body related to the passed in language.
     * @since 3.0.2
     */
    public String getBody(String language) {
        Message.Body body = this.getMessageBody(language);
        return body == null ? null : body.message;
    }

    private Message.Body getMessageBody(String language) {
        language = this.determineLanguage(language);
        for (Message.Body body : this.bodies) {
            if (language.equals(body.language)) {
                return body;
            }
        }
        return null;
    }

    /**
     * Returns a set of all bodies in this Message, including the default message body accessible
     * from {@link #getBody()}.
     *
     * @return a collection of all bodies in this Message.
     * @since 3.0.2
     */
    public Collection<Message.Body> getBodies() {
        return Collections.unmodifiableCollection(this.bodies);
    }

    /**
     * Sets the body of the message. The body is the main message contents.
     *
     * @param body the body of the message.
     */
    public void setBody(String body) {
        if (body == null) {
            this.removeBody(""); // use empty string because #removeBody(null) is ambiguous
            return;
        }
        this.addBody(null, body);
    }

    /**
     * Adds a body with a corresponding language.
     *
     * @param language the language of the body being added.
     * @param body the body being added to the message.
     * @return the new {@link Message.Body}
     * @throws NullPointerException if the body is null, a null pointer exception is thrown
     * @since 3.0.2
     */
    public Message.Body addBody(String language, String body) {
        language = this.determineLanguage(language);
        Message.Body messageBody = new Message.Body(language, body);
        this.bodies.add(messageBody);
        return messageBody;
    }

    /**
     * Removes the body with the given language from the message.
     *
     * @param language the language of the body which is to be removed
     * @return true if a body was removed and false if it was not.
     */
    public boolean removeBody(String language) {
        language = this.determineLanguage(language);
        for (Message.Body body : this.bodies) {
            if (language.equals(body.language)) {
                return this.bodies.remove(body);
            }
        }
        return false;
    }

    /**
     * Removes the body from the message and returns true if the body was removed.
     *
     * @param body the body being removed from the message.
     * @return true if the body was successfully removed and false if it was not.
     * @since 3.0.2
     */
    public boolean removeBody(Message.Body body) {
        return this.bodies.remove(body);
    }

    /**
     * Returns all the languages being used for the bodies, not including the default body.
     *
     * @return the languages being used for the bodies.
     * @since 3.0.2
     */
    public Collection<String> getBodyLanguages() {
        Message.Body defaultBody = this.getMessageBody(null);
        List<String> languages = new ArrayList<String>();
        for (Message.Body body : this.bodies) {
            if (!body.equals(defaultBody)) {
                languages.add(body.language);
            }
        }
        return Collections.unmodifiableCollection(languages);
    }

    /**
     * Returns the thread id of the message, which is a unique identifier for a sequence
     * of "chat" messages. If no thread id is set, <tt>null</tt> will be returned.
     *
     * @return the thread id of the message, or <tt>null</tt> if it doesn't exist.
     */
    public String getThread() {
        return this.thread;
    }

    /**
     * Sets the thread id of the message, which is a unique identifier for a sequence
     * of "chat" messages.
     *
     * @param thread the thread id of the message.
     */
    public void setThread(String thread) {
        this.thread = thread;
    }

    /**
     * Returns the xml:lang of this Message.
     *
     * @return the xml:lang of this Message.
     * @since 3.0.2
     */
    public String getLanguage() {
        return this.language;
    }

    /**
     * Sets the xml:lang of this Message.
     *
     * @param language the xml:lang of this Message.
     * @since 3.0.2
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    private String determineLanguage(String language) {

        // empty string is passed by #setSubject() and #setBody() and is the same as null
        language = "".equals(language) ? null : language;

        // if given language is null check if message language is set
        if (language == null && this.language != null) {
            return this.language;
        }
        else if (language == null) {
            return Packet.getDefaultLanguage();
        }
        else {
            return language;
        }

    }

    @Override
	public String toXML() {
        StringBuilder buf = new StringBuilder();
        buf.append("<message");
        if (this.getXmlns() != null) {
            buf.append(" xmlns=\"").append(this.getXmlns()).append("\"");
        }
        if (this.language != null) {
            buf.append(" xml:lang=\"").append(this.getLanguage()).append("\"");
        }
        if (this.getPacketID() != null) {
            buf.append(" id=\"").append(this.getPacketID()).append("\"");
        }
        if (this.getTo() != null) {
            buf.append(" to=\"").append(StringUtils.escapeForXML(this.getTo())).append("\"");
        }
        if (this.getFrom() != null) {
            buf.append(" from=\"").append(StringUtils.escapeForXML(this.getFrom())).append("\"");
        }
        if (this.type != Message.Type.normal) {
            buf.append(" type=\"").append(this.type).append("\"");
        }
        buf.append(">");
        // Add the subject in the default language
        Message.Subject defaultSubject = this.getMessageSubject(null);
        if(defaultSubject!=null){
        	buf.append("<subject>").append(StringUtils.escapeForXML(defaultSubject.getSubject()));
        	buf.append("</subject>");
        }
        //Add subjects in other languages
        for ( Message.Subject s : this.getSubjects()){
        	buf.append("<subject xml:lang=\""+s.getLanguage()+"\">");
        	buf.append(StringUtils.escapeForXML(s.getSubject()));
        	buf.append("</subject>");
        }
        // Add the body in the default language
        Message.Body defaultBody = this.getMessageBody(null);
        if (defaultBody != null) {
            buf.append("<body>").append(StringUtils.escapeForXML(defaultBody.message)).append("</body>");
        }
        // Add the bodies in other languages
        for (Message.Body body : this.getBodies()) {
            // Skip the default language
            if(body.equals(defaultBody))
                continue;
            buf.append("<body xml:lang=\"").append(body.getLanguage()).append("\">");
            buf.append(StringUtils.escapeForXML(body.getMessage()));
            buf.append("</body>");
        }
        if (this.thread != null) {
            buf.append("<thread>").append(this.thread).append("</thread>");
        }
        // Append the error subpacket if the message type is an error.
        if (this.type == Message.Type.error) {
            XMPPError error = this.getError();
            if (error != null) {
                buf.append(error.toXML());
            }
        }
        // Add packet extensions, if any are defined.
        buf.append(this.getExtensionsXML());
        buf.append("</message>");
        return buf.toString();
    }


    @Override
	public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if(!super.equals(message)) { return false; }
        if (this.bodies.size() != message.bodies.size() || !this.bodies.containsAll(message.bodies)) {
            return false;
        }
        if (this.language != null ? !this.language.equals(message.language) : message.language != null) {
            return false;
        }
        if (this.subjects.size() != message.subjects.size() || !this.subjects.containsAll(message.subjects)) {
            return false;
        }
        if (this.thread != null ? !this.thread.equals(message.thread) : message.thread != null) {
            return false;
        }
        return this.type == message.type;

    }

    @Override
	public int hashCode() {
        int result;
        result = this.type != null ? this.type.hashCode() : 0;
        result = 31 * result + this.subjects.hashCode();
        result = 31 * result + (this.thread != null ? this.thread.hashCode() : 0);
        result = 31 * result + (this.language != null ? this.language.hashCode() : 0);
        result = 31 * result + this.bodies.hashCode();
        return result;
    }

    /**
     * Represents a message subject, its language and the content of the subject.
     */
    public static class Subject {

        private final String subject;
        private final String language;

        private Subject(String language, String subject) {
            if (language == null) {
                throw new NullPointerException("Language cannot be null.");
            }
            if (subject == null) {
                throw new NullPointerException("Subject cannot be null.");
            }
            this.language = language;
            this.subject = subject;
        }

        /**
         * Returns the language of this message subject.
         *
         * @return the language of this message subject.
         */
        public String getLanguage() {
            return this.language;
        }

        /**
         * Returns the subject content.
         *
         * @return the content of the subject.
         */
        public String getSubject() {
            return this.subject;
        }


        @Override
		public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) { return false; }

            Message.Subject otherSubject = (Message.Subject) o;

            if (!this.language.equals(otherSubject.language)) {
                return false;
            }
            return this.subject.equals(otherSubject.subject);

        }

        @Override
		public int hashCode() {
            int result;
            result = this.subject.hashCode();
            result = 31 * result + this.language.hashCode();
            return result;
        }
    }

    /**
     * Represents a message body, its language and the content of the message.
     */
    public static class Body {

        private final String message;
        private final String language;

        private Body(String language, String message) {
            if (language == null) {
                throw new NullPointerException("Language cannot be null.");
            }
            if (message == null) {
                throw new NullPointerException("Message cannot be null.");
            }
            this.language = language;
            this.message = message;
        }

        /**
         * Returns the language of this message body.
         *
         * @return the language of this message body.
         */
        public String getLanguage() {
            return this.language;
        }

        /**
         * Returns the message content.
         *
         * @return the content of the message.
         */
        public String getMessage() {
            return this.message;
        }

        @Override
		public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) { return false; }

            Message.Body otherBody = (Message.Body) o;

            if (this.language != null ? !this.language.equals(otherBody.language) : otherBody.language != null) {
                return false;
            }
            return this.message.equals(otherBody.message);

        }

        @Override
		public int hashCode() {
            int result;
            result = this.message.hashCode();
            result = 31 * result + (this.language != null ? this.language.hashCode() : 0);
            return result;
        }

    }

    /**
     * Represents the type of a message.
     */
    public enum Type {

        /**
         * (Default) a normal text message used in email like interface.
         */
        normal,

        /**
         * Typically short text message used in line-by-line chat interfaces.
         */
        chat,

        /**
         * Chat message sent to a groupchat server for group chats.
         */
        groupchat,

        /**
         * Text message to be displayed in scrolling marquee displays.
         */
        headline,

        /**
         * indicates a messaging error.
         */
        error;

        public static Message.Type fromString(String name) {
            try {
                return valueOf(name);
            }
            catch (Exception e) {
                return Message.Type.normal;
            }
        }

    }
}