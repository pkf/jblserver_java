package cn.chenhub.intellij;

import cn.chenhub.JrebelUtil.JrebelSign;
import cn.chenhub.util.rsasign;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import net.sf.json.JSONObject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/*
 * Created by chenq on 2017/4/26,026.
 */
@RestController
public class RPCController {

    @RequestMapping("/jrebel/leases")
    public void jrebelLeases(String randomness,String username,String guid,String clientTime,boolean offline, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println(randomness+","+username+","+guid+","+clientTime+","+offline);
        String validFrom = "null";
        String validUntil = "null";
        if (offline) {
            //long clinetTimeUntil = Long.parseLong(clientTime) + Long.parseLong(offlineDays)  * 24 * 60 * 60 * 1000;
            long clinetTimeUntil = Long.parseLong(clientTime) + 180L * 24 * 60 * 60 * 1000;
            validFrom = clientTime;
            validUntil = String.valueOf(clinetTimeUntil);
        }
//        baseRequest.setHandled(true);
        String jsonStr = "{\n" +
                "    \"serverVersion\": \"3.2.4\",\n" +
                "    \"serverProtocolVersion\": \"1.1\",\n" +
                "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n" +
                "    \"groupType\": \"managed\",\n" +
                "    \"id\": 1,\n" +
                "    \"licenseType\": 1,\n" +
                "    \"evaluationLicense\": false,\n" +
                "    \"signature\": \"OJE9wGg2xncSb+VgnYT+9HGCFaLOk28tneMFhCbpVMKoC/Iq4LuaDKPirBjG4o394/UjCDGgTBpIrzcXNPdVxVr8PnQzpy7ZSToGO8wv/KIWZT9/ba7bDbA8/RZ4B37YkCeXhjaixpmoyz/CIZMnei4q7oWR7DYUOlOcEWDQhiY=\",\n" +
                "    \"serverRandomness\": \"H2ulzLlh7E0=\",\n" +
                "    \"seatPoolType\": \"standalone\",\n" +
                "    \"statusCode\": \"SUCCESS\",\n" +
                "    \"offline\": " + String.valueOf(offline) + ",\n" +
                "    \"validFrom\": " + validFrom + ",\n" +
                "    \"validUntil\": " + validUntil + ",\n" +
                "    \"company\": \"Administrator\",\n" +
                "    \"orderId\": \"\",\n" +
                "    \"zeroIds\": [\n" +
                "        \n" +
                "    ],\n" +
                "    \"licenseValidFrom\": 1490544001000,\n" +
                "    \"licenseValidUntil\": 1691839999000\n" +
                "}";

        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        if (randomness == null || username == null || guid == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } else {
            JrebelSign jrebelSign = new JrebelSign();
            jrebelSign.toLeaseCreateJson(randomness, guid, offline, validFrom, validUntil);
            String signature = jrebelSign.getSignature();
            jsonObject.put("signature", signature);
            jsonObject.put("company", username);
            String body = jsonObject.toString();
            response.getWriter().print(body);
        }
    }
    @RequestMapping("/jrebel/leases/1")
    public void jrebelLeases1(String username, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String jsonStr = "{\n" +
                "    \"serverVersion\": \"3.2.4\",\n" +
                "    \"serverProtocolVersion\": \"1.1\",\n" +
                "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n" +
                "    \"groupType\": \"managed\",\n" +
                "    \"statusCode\": \"SUCCESS\",\n" +
                "    \"msg\": null,\n" +
                "    \"statusMessage\": null\n" +
                "}\n";
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        if (username != null ) {
            jsonObject.put("company", username);
        }
        String body = jsonObject.toString();
        response.getWriter().print(body);

    }
    @RequestMapping("/agent/leases")
    public String agentLeases(){
        return "redirect:/jrebel/leases";
    }
    @RequestMapping("/agent/leases/1")
    public String agentLeases1(){
        return "redirect:/jrebel/leases/1";
    }
    @RequestMapping("/jrebel/validate-connection")
    public void jrebelValidate(HttpServletResponse response) throws IOException  {
        response.setContentType("application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String jsonStr = "{\n" +
                "    \"serverVersion\": \"3.2.4\",\n" +
                "    \"serverProtocolVersion\": \"1.1\",\n" +
                "    \"serverGuid\": \"a1b4aea8-b031-4302-b602-670a990272cb\",\n" +
                "    \"groupType\": \"managed\",\n" +
                "    \"statusCode\": \"SUCCESS\",\n" +
                "    \"company\": \"Administrator\",\n" +
                "    \"canGetLease\": true,\n" +
                "    \"licenseType\": 1,\n" +
                "    \"evaluationLicense\": false,\n" +
                "    \"seatPoolType\": \"standalone\"\n" +
                "}\n";
        JSONObject jsonObject = JSONObject.fromObject(jsonStr);
        String body = jsonObject.toString();
        response.getWriter().print(body);
    }

    @RequestMapping("/rpc/ping.action")

    public void ping(String salt, HttpServletResponse response ) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if(salt==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }else{
            String xmlContent = "<PingResponse><message></message><responseCode>OK</responseCode><salt>" + salt + "</salt></PingResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }

    }
    @RequestMapping("/rpc/obtainTicket.action")
        public void obtainTicket(String userName ,String salt, HttpServletResponse response ) throws Exception {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
//        SimpleDateFormat fm=new SimpleDateFormat("EEE,d MMM yyyy hh:mm:ss Z", Locale.ENGLISH);
//        String date =fm.format(new Date())+" GMT";
        //response.setHeader("Date", date);
        //response.setHeader("Server", "fasthttp");
        String prolongationPeriod = "607875500";
        if(salt==null||userName==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }else{
            String xmlContent = "<ObtainTicketResponse><message></message><prolongationPeriod>" + prolongationPeriod + "</prolongationPeriod><responseCode>OK</responseCode><salt>" + salt + "</salt><ticketId>1</ticketId><ticketProperties>licensee=" + userName + "\tlicenseType=0\t</ticketProperties></ObtainTicketResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }

    @RequestMapping("/rpc/releaseTicket.action")
    public void releaseTicket(String salt,HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        if(salt==null){
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }else{
            String xmlContent = "<ReleaseTicketResponse><message></message><responseCode>OK</responseCode><salt>" + salt + "</salt></ReleaseTicketResponse>";
            String xmlSignature = rsasign.Sign(xmlContent);
            String body = "<!-- " + xmlSignature + " -->\n" + xmlContent;
            response.getWriter().print(body);
        }
    }


}
