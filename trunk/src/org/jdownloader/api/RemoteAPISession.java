package org.jdownloader.api;

import jd.controlling.UniqueID;

import org.appwork.utils.net.httpserver.session.HttpSession;
import org.appwork.utils.net.httpserver.session.HttpSessionController;

public class RemoteAPISession implements HttpSession {

    private boolean                                      alive      = true;
    private String                                       sessionID  = null;
    private HttpSessionController<? extends HttpSession> controller = null;

    protected RemoteAPISession(HttpSessionController<? extends HttpSession> controller) {
        this.sessionID = "" + new UniqueID() + ("_" + System.currentTimeMillis()).hashCode() + System.currentTimeMillis();
        this.controller = controller;
    }

    public HttpSessionController<? extends HttpSession> getSessionController() {
        return controller;
    }

    public String getSessionID() {
        return sessionID;
    }

    public boolean isAlive() {
        return alive;
    }

    protected void setAlive(boolean b) {
        this.alive = b;
    }

}