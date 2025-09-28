package myex.shopping.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {


    //Form + ModelAttribute 는 BindingResult로 처리.

/*   안됨. 전역으로 잡을려면 Valid + BindingResult를 포기.
// 단일 값 검증 처리는 컨트롤러 안에서 처리 하기로 변경.
 //@PathVariable 검증을 위해서
    @ExceptionHandler(ConstraintViolationException.class )
    public String handleConstraintViolation(ConstraintViolationException ex,
                                            HttpServletRequest request,
                                            RedirectAttributes redirectAttributes){

        System.out.println("GlobalExceptionHandler.handleConstraintViolation");

        //map : Stream 안의 요소 변환.
        String errorMessage = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));//위반이 여러 개 어노테이션일 경우 쉼표로 구분.

        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        //이전 페이지로 리다이렉트
        String referer = request.getHeader("Referer");
        if (referer !=null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/main"; //fallback (없을때 대체 페이지)
        }

    }

    //@RequestParam 검증 위해서.
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public String handleMissingParam(MissingServletRequestParameterException ex,
                                     HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {

        System.out.println("GlobalExceptionHandler.handleMissingParam");

        String parameterName = ex.getParameterName();

        String errorMessage = "필수 요청 파라미터 '" + parameterName +"'가 누락되었습니다.";
        redirectAttributes.addFlashAttribute("errorMessage", errorMessage);

        String referer = request.getHeader("Referer");
        if (referer !=null && !referer.isEmpty()) {
            return "redirect:" + referer;
        } else {
            return "redirect:/main"; //fallback (없을때 대체 페이지)
        }


    }*/

    //타입 미스매치, 뷰로 뿌리는건 나중에 변경.
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
