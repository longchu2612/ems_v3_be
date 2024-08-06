package com.ems.application.service.payment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ems.application.dto.payment.PaymentRequest;
import com.ems.application.dto.payment.PaymentResponse;
import com.ems.application.service.BaseService;
import com.ems.config.PaymentConfig;

@Service
public class PaymentService extends BaseService {
    public PaymentService() {
    }

    public ResponseEntity<PaymentResponse> createPayment(PaymentRequest paymentRequest, HttpServletRequest req)
            throws UnsupportedEncodingException {
        String vnp_Version = PaymentConfig.vnp_Version;
        String vnp_Command = "pay";
        String orderType = PaymentConfig.orderType;
        long amount = paymentRequest.getAmount();
        String bankCode = PaymentConfig.bankCode;
        String vnp_TxnRef = paymentRequest.getOrderId();
        String vnp_IpAddr = PaymentConfig.getIpAddress(req);

        String vnp_TmnCode = PaymentConfig.vnp_TmnCode;
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        String locate = paymentRequest.getLanguage();
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", PaymentConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        // cld.add(Calendar.MINUTE, 15);
        // String vnp_ExpireDate = formatter.format(cld.getTime());
        // vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue,
                        StandardCharsets.US_ASCII.toString()));
                // Build query
                query.append(URLEncoder.encode(fieldName,
                        StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue,
                        StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        System.err.println(vnp_Params.get("vnp_OrderInfo"));
        System.err.println(vnp_Params.get("vnp_ReturnUrl"));
        String queryUrl = query.toString();
        String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey,
                hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = PaymentConfig.vnp_PayUrl + "?" + queryUrl;
        PaymentResponse job = new PaymentResponse();
        job.setCode("00");
        job.setMessage("success");
        job.setData(paymentUrl);
        return ResponseEntity.ok(job);
    }

    // public ResponseEntity<IPNResponse> getPaymentDetail(HttpServletRequest req)
    // throws MalformedURLException {
    // String vnp_RequestId = PaymentConfig.getRandomNumber(8);
    // String vnp_Version = "2.1.0";
    // String vnp_Command = "querydr";
    // String vnp_TmnCode = PaymentConfig.vnp_TmnCode;
    // String vnp_TxnRef = req.getParameter("order_id");
    // String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + vnp_TxnRef;
    // // String vnp_TransactionNo = req.getParameter("transactionNo");
    // String vnp_TransDate = req.getParameter("trans_date");

    // Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    // SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    // String vnp_CreateDate = formatter.format(cld.getTime());

    // String vnp_IpAddr = PaymentConfig.getIpAddress(req);

    // JsonObject vnp_Params = new JsonObject();

    // vnp_Params.addProperty("vnp_RequestId", vnp_RequestId);
    // vnp_Params.addProperty("vnp_Version", vnp_Version);
    // vnp_Params.addProperty("vnp_Command", vnp_Command);
    // vnp_Params.addProperty("vnp_TmnCode", vnp_TmnCode);
    // vnp_Params.addProperty("vnp_TxnRef", vnp_TxnRef);
    // vnp_Params.addProperty("vnp_OrderInfo", vnp_OrderInfo);
    // // vnp_Params.put("vnp_TransactionNo", vnp_TransactionNo);
    // vnp_Params.addProperty("vnp_TransactionDate", vnp_TransDate);
    // vnp_Params.addProperty("vnp_CreateDate", vnp_CreateDate);
    // vnp_Params.addProperty("vnp_IpAddr", vnp_IpAddr);

    // String hash_Data = String.join("|", vnp_RequestId, vnp_Version, vnp_Command,
    // vnp_TmnCode, vnp_TxnRef,
    // vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);
    // String vnp_SecureHash = PaymentConfig.hmacSHA512(PaymentConfig.secretKey,
    // hash_Data.toString());

    // vnp_Params.addProperty("vnp_SecureHash", vnp_SecureHash);

    // URL url = new URL(PaymentConfig.vnp_ApiUrl);
    // HttpURLConnection con = (HttpURLConnection) url.openConnection();
    // con.setRequestMethod("POST");
    // con.setRequestProperty("Content-Type", "application/json");
    // con.setDoOutput(true);
    // DataOutputStream wr = new DataOutputStream(con.getOutputStream());
    // wr.writeBytes(vnp_Params.toString());
    // wr.flush();
    // wr.close();
    // int responseCode = con.getResponseCode();
    // System.out.println("nSending 'POST' request to URL : " + url);
    // System.out.println("Post Data : " + vnp_Params);
    // System.out.println("Response Code : " + responseCode);
    // BufferedReader in = new BufferedReader(
    // new InputStreamReader(con.getInputStream()));
    // String output;
    // StringBuffer response = new StringBuffer();
    // while ((output = in.readLine()) != null) {
    // response.append(output);
    // }
    // in.close();
    // System.out.println(response.toString());
    // return ResponseEntity.ok(new IPNResponse());
    // }
}
