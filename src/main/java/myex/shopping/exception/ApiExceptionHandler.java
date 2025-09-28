package myex.shopping.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
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

@RestControllerAdvice("myex.shopping.controller.api") //범위 지정
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

    //@PathVariable 검증을 위해서. + @RequestParam null 방지.
    /*
    @RequestParam을 처리할 때, Spring은 말씀하신 대로 단계적으로 확인합니다.
     1. 1단계: 파라미터 존재 여부 확인(MissingServletRequestParameterException)
     2. 2단계: 값 유효성 검증(ConstraintViolationException)
     */
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

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String,String>> handleTypeMismatch(TypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        String field = (ex.getPropertyName() == null) ? null : ex.getPropertyName(); //파라미터 명. Long id
        String invalidValue = (ex.getValue() == null) ? null : ex.getValue().toString(); //무효 요청 값.
        String requiredType = (ex.getRequiredType() == null) ? null : ex.getRequiredType().getSimpleName();
        error.put(field, String.format("'%s'은(는) %s 타입으로 변환할 수 없습니다.", invalidValue, requiredType));

        return ResponseEntity.badRequest().body(error);

    }
}
