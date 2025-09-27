package myex.shopping.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    //DTO 검증 실패 -
    // @RequestBody - JSON 검사 (MethodArgumentNotValidException)
    // @ModelAttribute - Form (form-data, x-www-form)데이터 검사(BindException)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<Map<String, String>> handleValidation(Exception ex) {
        
        BindingResult bindingResult;
        if (ex instanceof MethodArgumentNotValidException manv) {
            bindingResult = manv.getBindingResult();
        } else if (ex instanceof BindException be) {
            bindingResult = be.getBindingResult();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); //500 에러.
        }

        Map<String, String> errors = new HashMap<>();
        bindingResult.getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    //@PathVariable 검증을 위해서.
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, List<String>>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, List<String>> errors = new HashMap<>();
        ex.getConstraintViolations().
                forEach(error -> {
                    String field = error.getPropertyPath().toString();
                    //메소드명.파라미터 이름 (위치 특정)
                    errors.computeIfAbsent(field, k -> new ArrayList<>()).add(error.getMessage());
                });

        return ResponseEntity.badRequest().body(errors);
    }

    //@RequestParam(required =true)라서 사용.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParam(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put(ex.getParameterName(), "필드를 작성해주세요.");
        return ResponseEntity.badRequest().body(error);
    }
}
