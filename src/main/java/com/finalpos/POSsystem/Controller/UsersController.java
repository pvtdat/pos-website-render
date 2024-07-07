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

    // My code
    @GetMapping("/")
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

    // My code
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

    // My code
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

            String htmlContent = "<!-- © 2018 Shift Technologies. All rights reserved. -->\n" +
                    "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"table-layout:fixed;background-color:#f9f9f9\" id=\"bodyTable\">\n" +
                    "    <tbody>\n" +
                    "        <tr>\n" +
                    "            <td style=\"padding-right:10px;padding-left:10px;\" align=\"center\" valign=\"top\" id=\"bodyCell\">\n" +
                    "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"wrapperWebview\" style=\"max-width:600px\">\n" +
                    "                    <tbody>\n" +
                    "                        <tr>\n" +
                    "                            <td align=\"center\" valign=\"top\">\n" +
                    "                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                    "                                    <tbody>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-top: 20px; padding-bottom: 20px; padding-right: 0px;\" align=\"right\" valign=\"middle\" class=\"webview\">\n" +
                    "                                                <a href=\"https://pos-website-nine.vercel.app/\" style=\"color:#bbb;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:12px;font-weight:400;font-style:normal;letter-spacing:normal;line-height:20px;text-transform:none;text-align:right;text-decoration:underline;padding:0;margin:0\" target=\"_blank\" class=\"text hideOnMobile\">Oh wait, there's more! →</a>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                    </tbody>\n" +
                    "                                </table>\n" +
                    "                            </td>\n" +
                    "                        </tr>\n" +
                    "                    </tbody>\n" +
                    "                </table>\n" +
                    "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"wrapperBody\" style=\"max-width:600px\">\n" +
                    "                    <tbody>\n" +
                    "                        <tr>\n" +
                    "                            <td align=\"center\" valign=\"top\">\n" +
                    "                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"tableCard\" style=\"background-color:#fff;border-color:#e5e5e5;border-style:solid;border-width:0 1px 1px 1px;\">\n" +
                    "                                    <tbody>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"background-color:#f36f21;font-size:1px;line-height:3px\" class=\"topBorder\" height=\"3\">&nbsp;</td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-top: 60px; padding-bottom: 20px;\" align=\"center\" valign=\"middle\" class=\"emailLogo\">\n" +
                    "                                                <a href=\"#\" style=\"text-decoration:none\" target=\"_blank\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/hero-img/blue/logo.png\" style=\"width:100%;max-width:150px;height:auto;display:block\" width=\"150\">\n" +
                    "                                                </a>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-bottom: 20px;\" align=\"center\" valign=\"top\" class=\"imgHero\">\n" +
                    "                                                <a href=\"#\" style=\"text-decoration:none\" target=\"_blank\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/hero-img/blue/heroGradient/user-account.png\" style=\"width:100%;max-width:600px;height:auto;display:block;color: #f9f9f9;\" width=\"600\">\n" +
                    "                                                </a>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-bottom: 5px; padding-left: 20px; padding-right: 20px;\" align=\"center\" valign=\"top\" class=\"mainTitle\">\n" +
                    "                                                <h2 class=\"text\" style=\"color:#000;font-family:Poppins,Helvetica,Arial,sans-serif;font-size:28px;font-weight:500;font-style:normal;letter-spacing:normal;line-height:36px;text-transform:none;text-align:center;padding:0;margin:0\">Hi " + user.getName() + "</h2>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-bottom: 30px; padding-left: 20px; padding-right: 20px;\" align=\"center\" valign=\"top\" class=\"subTitle\">\n" +
                    "                                                <h4 class=\"text\" style=\"color:#999;font-family:Poppins,Helvetica,Arial,sans-serif;font-size:16px;font-weight:500;font-style:normal;letter-spacing:normal;line-height:24px;text-transform:none;text-align:center;padding:0;margin:0\">Verify Your Email Account</h4>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-left:20px;padding-right:20px\" align=\"center\" valign=\"top\" class=\"containtTable ui-sortable\">\n" +
                    "                                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"tableDescription\">\n" +
                    "                                                    <tbody>\n" +
                    "                                                        <tr>\n" +
                    "                                                            <td style=\"padding-bottom: 14px;\" align=\"center\" valign=\"top\" class=\"description\">\n" +
                    "                                                                <p class=\"text\" style=\"color:#666;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:14px;font-weight:400;font-style:normal;letter-spacing:normal;line-height:22px;text-transform:none;text-align:center;padding:0;margin:0\">Your registration was successful!</p>\n" +
                    "                                                            </td>\n" +
                    "                                                        </tr>\n" +
                    "                                                    </tbody>\n" +
                    "                                                </table>\n" +
                    "                                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"tableButton\">\n" +
                    "                                                    <tbody>\n" +
                    "                                                        <tr>\n" +
                    "                                                            <td style=\"padding-top:20px;padding-bottom:20px\" align=\"center\" valign=\"top\">\n" +
                    "                                                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" align=\"center\">\n" +
                    "                                                                    <tbody>\n" +
                    "                                                                        <tr>\n" +
                    "                                                                            <td style=\"background-color: rgb(0, 210, 244); padding: 12px 35px; border-radius: 50px;\" align=\"center\" class=\"ctaButton\">\n" +
                    "                                                                                <a href=\"" + SERVER_ADDRESS + "/direct/?token=" + token + "\" style=\"color:#fff;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:14px;font-weight:600;font-style:normal;letter-spacing:normal;line-height:20px;text-transform:uppercase;text-align:center;text-decoration:none;display:block\" target=\"_blank\" class=\"text\">Confirm Email</a>\n" +
                    "                                                                            </td>\n" +
                    "                                                                        </tr>\n" +
                    "                                                                    </tbody>\n" +
                    "                                                                </table>\n" +
                    "                                                            </td>\n" +
                    "                                                        </tr>\n" +
                    "                                                    </tbody>\n" +
                    "                                                </table>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"font-size:1px;line-height:1px\" height=\"20\">&nbsp;</td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td align=\"center\" valign=\"top\" class=\"socialLinks\">\n" +
                    "                                                <a href=\"https://www.facebook.com/pvtd.2003/\" style=\"display: inline-block;\" target=\"_blank\" class=\"facebook\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/social/light/facebook.png\" style=\"height:auto;width:100%;max-width:40px;margin-left:2px;margin-right:2px\" width=\"40\">\n" +
                    "                                                </a>\n" +
                    "                                                <a href=\"#\" style=\"display: inline-block;\" target=\"_blank\" class=\"instagram\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/social/light/instagram.png\" style=\"height:auto;width:100%;max-width:40px;margin-left:2px;margin-right:2px\" width=\"40\">\n" +
                    "                                                </a>\n" +
                    "                                                <a href=\"www.linkedin.com/in/datphamvantien\" style=\"display: inline-block;\" target=\"_blank\" class=\"linkdin\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/social/light/linkdin.png\" style=\"height:auto;width:100%;max-width:40px;margin-left:2px;margin-right:2px\" width=\"40\">\n" +
                    "                                                </a>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding: 10px 10px 5px;\" align=\"center\" valign=\"top\" class=\"brandInfo\">\n" +
                    "                                                <p class=\"text\" style=\"color:#bbb;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:12px;font-weight:400;font-style:normal;letter-spacing:normal;line-height:20px;text-transform:none;text-align:center;padding:0;margin:0\">©&nbsp;FinnIT | Tran Xuan Soan Street | District 7, HCMC, Viet Nam.</p>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding: 0px 10px 10px;\" align=\"center\" valign=\"top\" class=\"footerEmailInfo\">\n" +
                    "                                                <p class=\"text\" style=\"color:#bbb;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:12px;font-weight:400;font-style:normal;letter-spacing:normal;line-height:20px;text-transform:none;text-align:center;padding:0;margin:0\">\n" +
                    "                                                    If you have any questions please contact us <a href=\"mailto:phamvantiendat.work@gmail.com\" style=\"color:#bbb;text-decoration:underline\" target=\"_blank\">phamvantiendat.work@gmail.com</a>.\n" +
                    "                                                    <br>\n" +
                    "                                                </p>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding-top:10px;padding-bottom:10px;padding-left:10px;padding-right:10px\" align=\"center\" valign=\"top\" class=\"appLinks\">\n" +
                    "                                                <a href=\"#Play-Store-Link\" style=\"display: inline-block;\" target=\"_blank\" class=\"play-store\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/app/play-store.png\" style=\"height:auto;margin:5px;width:100%;max-width:120px\" width=\"120\">\n" +
                    "                                                </a>\n" +
                    "                                                <a href=\"#App-Store-Link\" style=\"display: inline-block;\" target=\"_blank\" class=\"app-store\">\n" +
                    "                                                    <img alt=\"\" border=\"0\" src=\"http://email.aumfusion.com/vespro/img/app/app-store.png\" style=\"height:auto;margin:5px;width:100%;max-width:120px\" width=\"120\">\n" +
                    "                                                </a>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"font-size:1px;line-height:1px\" height=\"30\">&nbsp;</td>\n" +
                    "                                        </tr>\n" +
                    "                                    </tbody>\n" +
                    "                                </table>\n" +
                    "                            </td>\n" +
                    "                        </tr>\n" +
                    "                        <tr>\n" +
                    "                            <td style=\"line-height:1px;min-width:600px;background-color:#f9f9f9\" align=\"center\" valign=\"top\" class=\"emailBackground\">\n" +
                    "                                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" class=\"tableCard\" style=\"max-width:600px\">\n" +
                    "                                    <tbody>\n" +
                    "                                        <tr>\n" +
                    "                                            <td style=\"padding: 0px 20px 20px;\" align=\"center\" valign=\"top\" class=\"footerCopyRights\">\n" +
                    "                                                <p class=\"text\" style=\"color:#bbb;font-family:'Open Sans',Helvetica,Arial,sans-serif;font-size:12px;font-weight:400;font-style:normal;letter-spacing:normal;line-height:20px;text-transform:none;text-align:center;padding:0;margin:0\">© 2024 FinnIT. All rights reserved.</p>\n" +
                    "                                            </td>\n" +
                    "                                        </tr>\n" +
                    "                                    </tbody>\n" +
                    "                                </table>\n" +
                    "                            </td>\n" +
                    "                        </tr>\n" +
                    "                    </tbody>\n" +
                    "                </table>\n" +
                    "            </td>\n" +
                    "        </tr>\n" +
                    "    </tbody>\n" +
                    "</table>";

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(stringReceiverEmail));
            mimeMessage.setSubject("POS Website");
            mimeMessage.setContent(htmlContent, "text/html; charset=utf-8");
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