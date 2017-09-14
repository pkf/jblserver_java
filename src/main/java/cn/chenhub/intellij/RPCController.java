package cn.chenhub.intellij;

import cn.chenhub.util.RSAUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;


/*
 * Created by chenq on 2017/4/26,026.
 */
@RestController
@RequestMapping("/rpc")
public class RPCController {
    private RSAUtil rsaUtil = new RSAUtil();

    @RequestMapping("/obtainTicket.action")
    public String obtainTicket(String salt, String userName) throws Exception {
        String content = "<ObtainTicketResponse>" +
                "<message></message>" +
                "<prolongationPeriod>607875500</prolongationPeriod>" +
                "<responseCode>OK</responseCode>" +
                "<salt>" + salt + "</salt>" +
                "<ticketId>1</ticketId>" +
                "<ticketProperties>licensee=" + userName + "	licenseType=0	</ticketProperties>" +
                "</ObtainTicketResponse>";
        return rsaUtil.rsaSign(content);
    }

    @RequestMapping("/releaseTicket.action")
    public void releaseTicket(HttpServletResponse httpServletResponse) {
        httpServletResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @RequestMapping("ping.action")
    public String ping(String salt) throws Exception {
        String content = "<PingResponse>" +
                "<message></message>" +
                "<responseCode>OK</responseCode>" +
                "<salt>" + salt + "</salt>" +
                "</PingResponse>";
        return rsaUtil.rsaSign(content);
    }

}
