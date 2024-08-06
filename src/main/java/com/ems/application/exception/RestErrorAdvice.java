package com.ems.application.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ems.application.dto.base.BaseResponse;
import com.ems.application.enums.ErrorMessageType;

@Slf4j
@RestControllerAdvice
public class RestErrorAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> handleMethodArgumentNotValidException(
            HttpServletRequest req,
            MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        BaseResponse body = new BaseResponse();
        body.setType(ErrorMessageType.SHOW_IN_MESSAGE);
        body.setStatus(HttpStatus.BAD_REQUEST.value());
        /*
         * body.setTitle(Objects.nonNull(ex.getCause()) &&
         * StringUtils.hasText(ex.getCause().getMessage()) ? ex.getCause().getMessage()
         * : ex.getMessage());
         */

        HashMap<String, List<Object>> messages = new HashMap<>();
        HashMap<String, JSONArray> subMessage = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            try {
                String indexPattern = "([^\\[]*)\\[([0-9]*)\\].([^\\[]*)";
                Pattern r = Pattern.compile(indexPattern);
                Matcher m = r.matcher(fieldName);
                if (m.matches()) {
                    String key = m.group(1);
                    int index = Integer.parseInt(m.group(2));
                    String subKey = m.group(3);
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(subKey, errorMessage);
                    jsonArray.put(jsonObject);
                    String subMessageKey = key + "-" + index;
                    if (!subMessage.containsKey(subMessageKey)) {
                        subMessage.put(subMessageKey, jsonArray);
                    } else {
                        subMessage.get(subMessageKey).put(jsonObject);
                    }

                    if (!messages.containsKey(key)) {
                        messages.put(key, new ArrayList<>());
                    }
                } else {
                    if (!messages.containsKey(fieldName)) {
                        messages.put(fieldName, new LinkedList<>(Arrays.asList(errorMessage)));
                    } else {
                        messages.get(fieldName).add(errorMessage);
                    }
                }
            } catch (Exception ignored) {

            }
        });
        ex.getBindingResult().getGlobalErrors().forEach(objectError -> {
            String fieldName;
            if (Objects.requireNonNull(objectError.getArguments()).length > 1) {
                fieldName = ((MessageSourceResolvable) objectError.getArguments()[1]).getDefaultMessage();
            } else {
                fieldName = objectError.getObjectName();
            }
            String errorMessage = objectError.getDefaultMessage();
            if (!messages.containsKey(fieldName)) {
                messages.put(fieldName, new LinkedList<>(Arrays.asList(errorMessage)));
            } else {
                messages.get(fieldName).add(errorMessage);
            }
        });
        if (!subMessage.isEmpty()) {
            subMessage.forEach((subKey, objects) -> {
                String key = subKey.substring(0, subKey.indexOf("-"));
                List<Object> objectList = new ArrayList<>();

                objects.forEach(o -> {
                    JSONObject jsonObject = (JSONObject) o;
                    objectList.add(jsonObject.toMap());
                });
                messages.get(key).add(objectList);

            });
            body.setMessages(messages);
        } else {
            body.setMessages(messages);
        }
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse> handleAccessDeniedException(HttpServletRequest req, Exception e) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        BaseResponse body = new BaseResponse();
        body.setType(ErrorMessageType.SHOW_IN_TITLE);
        body.setTitle(e.getMessage());
        body.setStatus(HttpStatus.UNAUTHORIZED.value());
        return new ResponseEntity<>(body, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleException(HttpServletRequest req, Exception e) {
        log.error(e.getMessage(), e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        BaseResponse body = new BaseResponse();
        body.setType(ErrorMessageType.SHOW_IN_TITLE);
        body.setTitle(e.getMessage());
        body.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(body, status);
    }
}
