package ui.controller;

import domain.model.NotAuthorizedException;
import domain.model.Person;
import ui.util.Authorization;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogInHandler extends RequestHandler {
    @Override
    public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, NotAuthorizedException, IOException {
        Person.Role[] roles = {Person.Role.guest};
        Authorization.checkrole(request, roles);

        String userid = request.getParameter("userid");
        Person person = getDB().findPerson(userid);

        boolean loginAccept = false;
        String password = request.getParameter("password");

        if (person == null){
            request.setAttribute("errors", "Username not found");
            //IRL is dit niet veilig om een error te tonen dat de userid bestaat maar swat
            System.out.println("Login attempt failed, incorrect username");
        } else if (person != null && !password.isEmpty()){
            loginAccept = person.isCorrectPassword(password);
            System.out.println("Logged in as " + person.toString());

            if (loginAccept){
                request.getSession().setAttribute("person", person);
                request.getSession().setAttribute("nextMessage", "Successfully logged in");
                return "RedirectServlet?command=Profile";
            } else {
                request.setAttribute("userIdPreviousValue", userid);
                request.setAttribute("errors", "Password Incorrect");
                System.out.println("Login attempt failed, incorrect password!");
            }
        }
        return "profile.jsp";
    }
}
