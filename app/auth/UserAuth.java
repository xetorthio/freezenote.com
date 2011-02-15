package auth;

import models.User;
import play.mvc.Scope.Session;

public class UserAuth {

    public static boolean isUserLoggedIn() {
        Session session = Session.current();
        if (!session.contains("user")) {
            return false;
        }
        User user = UserAuth.getUser();
        if (user == null) {
            session.clear();
            return false;
        }
        return true;
    }

    public static User getUser() {
        Session session = Session.current();
        String userId = session.get("user");
        if(userId == null) {
            return null;
        }
        User user = User.findById(Long.parseLong(userId));
        return user;
    }

    public static User doLogin(String email) {
        Session session = Session.current();
        User user = User.login(email);
        if (user == null) {
            user = new User();
            user.email = email;
            user.save();
        }

        session.put("user", user.id);
        return user;
    }

    public static void doLogout() {
        Session.current().clear();
    }

}
