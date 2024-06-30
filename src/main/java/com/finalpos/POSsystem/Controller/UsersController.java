package com.finalpos.POSsystem.Controller;

import com.finalpos.POSsystem.Model.UserModel;
import com.finalpos.POSsystem.Model.UserRepository;
import com.finalpos.POSsystem.Model.Package;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.finalpos.POSsystem.Controller.AccountController.JWT_Key;


@Controller
@RestController
@ResponseBody
@RequestMapping("/api/users")

public class UsersController {
    @Autowired
    UserRepository db;
    @Value("${default.application.avatar}")
    private String defaultAvatar;

    PasswordEncoder passwordEndcoder = new BCryptPasswordEncoder();

    @Value("${default.application.SERVER_ADDRESS}")
    private String SERVER_ADDRESS;

    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";


    @GetMapping("/")
    //    Get list of users
    public Package index(@RequestParam Optional<String> page) {
        try {
            int pageSize = 10;
            int pageNumber = 1;
            if(!page.isEmpty() && page.get() != "null") {
                pageNumber = Integer.parseInt(page.get());
            }
            int skipAmount = (pageNumber - 1) * pageSize;
            int totalUsers = (int) db.count();
            int totalPages = (int) Math.ceil((double) totalUsers / pageSize);


            List<UserModel> userList = db.findAll();
            List<UserModel> user = new ArrayList<>();

            // Check num of users in the last page
            // It will continue() when page + 1 (skipAmount > size()) -> reduce run time
            int endIdx = Math.min(skipAmount + pageSize, userList.size());
            for (int i = skipAmount; i < endIdx; i++) {
                user.add(userList.get(i));
            }

            Object data = new Object() {
                public final List<UserModel> users = user;
                public final int divider = totalPages;
            };
            return new Package(0, "success", data);
        }
        catch (Exception e) {
            return new Package(404, e.getMessage(), null);
        }
    }

    @PostMapping("/register")
    public Package register(@RequestParam("name") String name,
                            @RequestParam("email") String email) {
        if (!isValidEmail(email)) {
            return new Package(400, "Invalid email format", null);
        }
        if (db.findByEmail(email) != null) {
            return new Package(400, "Email already exist", null);
        }
        try {
            String username = email.split("@")[0];

            UserModel newUser = new UserModel();
            newUser.setName(name);
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setRole("Sale person");
            newUser.setImage(defaultAvatar);
            newUser.setStatus("InActive");
            newUser.setCreated_at(java.time.LocalDateTime.now());

            String defaultPassword = username;

            newUser.setPassword(passwordEndcoder.encode(defaultPassword));

            String tokenString = generateToken(newUser);

            sendRegistrationEmail(newUser, tokenString);
            db.save(newUser);

            return new Package(0, "Registration success", newUser);
        } catch (Exception e) {
            return new Package(404, e.getMessage(), null);
        }
    }


    @PostMapping("/resend")
    public Package resendEmail(@RequestParam("email") String email) {
        try {
            UserModel user = db.findByEmail(email);

            if (user != null) {
                user.setStatus("InActive");
                String token = generateToken(user);

                sendRegistrationEmail(user, token);
                db.save(user);
                return new Package(0, "Resend email successfully", null);
            } else {
                return new Package(404, "User not found", null);
            }
        } catch (Exception e) {
            return new Package(500, e.getMessage(), null);
        }
    }

    @PutMapping("/{userId}")
    public Package update(@PathVariable("userId") String userId,
                          @RequestParam String email,
                          @RequestParam String role,
                          @RequestParam String status){
        try {
            // Status: Active, InActive, Lock
            // Role: Administrator, Sale Person
            UserModel userModel = db.findUserModelById(userId);
            userModel.setEmail(email);
            userModel.setRole(role);
            userModel.setStatus(status);
            UserModel updateUser = db.save(userModel);
            return new Package(0, "success", updateUser);
        }
        catch (Exception e){
            return new Package(404, e.getMessage(), null);
        }
    }

    @DeleteMapping("/{userId}")
    public Package delete(@PathVariable("userId") String userId){
        try {
            UserModel removeUser = db.removeUserModelById(userId);
            return new Package(0, "success", removeUser);
        }
        catch (Exception e){
            return new Package(404, e.getMessage(), null);
        }
    }

    private void sendRegistrationEmail(UserModel user, String token) {
        try {
            String stringSenderEmail = "vate202@gmail.com";
            String stringReceiverEmail = user.getEmail();
            String stringPasswordSenderEmail = "lktyqjjjbiyefldc";

            String stringHost = "smtp.gmail.com";

            Properties properties = System.getProperties();


            properties.put("mail.smtp.host", stringHost);
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(stringSenderEmail, stringPasswordSenderEmail);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));

            mimeMessage.setSubject("Subject: Java App email");
            mimeMessage.setText("Hello " + user.getName() + ",\n\nYour registration was successful. Welcome to the Programmer World!"
            + "\n\nPlease click the link below to activate your account:\n"
                    + SERVER_ADDRESS+"direct/?token=" + token
                    + "\n\nThank you,\nJava App Team");

            Transport.send(mimeMessage);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private String generateToken(UserModel user) {
        Date expirationTime = new Date(System.currentTimeMillis() + 60000);
        return Jwts.builder()
                .claim("username", user.getUsername())
                .claim("name", user.getName())
                .claim("email", user.getEmail())
                .claim("image", user.getImage())
                .claim("role", user.getRole())
                .claim("status", user.getStatus())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, JWT_Key)
                .compact();
    }
}