package controllers;

import play.*;
import play.mvc.*;

import utils.LockManager;
import views.html.*;

public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result trylock(String key) {
        try {
            Boolean result = LockManager.acquireLock(key);
            if (result)
                return ok("Lock acquired.");
            else
                return status(409,"Lock is already taken");
        } catch (Exception e){
            return internalServerError("Internal error: "+e.toString());
        }
    }

    public static Result tryAndRelease(String key) {
        try {
            LockManager.releaseLock(key);
            return ok(index.render("Lock acquired."));
        } catch (Exception e){
            return internalServerError("Internal error: "+e.toString());
        }
    }

}
