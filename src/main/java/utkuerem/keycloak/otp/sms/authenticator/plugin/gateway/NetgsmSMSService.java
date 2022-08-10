package utkuerem.keycloak.otp.sms.authenticator.plugin.gateway;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class NetgsmSMSService implements SmsService {

    boolean hideHeader = false;

    String username = "your-username";
    String password = "your-password";
    String header = "your-header";

    @Override
    public void send(String phoneNumber, String message) {
        try {
            // gönderilecek mesaj
//            body = body.replaceAll("\\n", "\\\\\n");
            String msg = URLEncoder.encode(message, StandardCharsets.UTF_8.toString());
            msg = msg.replaceAll("%0A", "%5Cn");
            // mesajın gönderileceği telefon numaraları
            String gsm = URLEncoder.encode(phoneNumber.replace("+90", ""), StandardCharsets.UTF_8.toString());

            String _header = URLEncoder.encode(header, StandardCharsets.UTF_8.toString());
            if (hideHeader)
                _header = URLEncoder.encode(username, StandardCharsets.UTF_8.toString());

            // Get ile sorgulanacak Netgsm servisi
            String url = "https://api.netgsm.com.tr/sms/send/get/?usercode=" + username + "&password=" + password + "&gsmno=" + gsm + "&message=" + msg + "&msgheader=" + _header + "&dil=TR";

            // URL obje tanımı
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // bağlantı türü tanımı
            con.setRequestMethod("GET");

            // bağlantı için header tanımı
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            // servise bağlanılıyor ve sonuç alınıyor
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
