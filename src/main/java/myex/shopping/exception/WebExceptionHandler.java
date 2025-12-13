package myex.shopping.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "myex.shopping.controller.web")
public class WebExceptionHandler {
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

    //타입 미스매치, 뷰로 뿌리는건 나중에 변경. (전체 추가해야 해서 hmtl layout 사용 예정)
    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatch(TypeMismatchException ex, Model model) {
        Map<String, String> error = new HashMap<>();
        String field = (ex.getPropertyName() == null) ? null : ex.getPropertyName(); //파라미터 명. Long id
        String invalidValue = (ex.getValue() == null) ? null : ex.getValue().toString(); //무효 요청 값.
        String requiredType = (ex.getRequiredType() == null) ? null : ex.getRequiredType().getSimpleName();
        error.put(field, String.format("'%s'은(는) %s 타입으로 변환할 수 없습니다.", invalidValue, requiredType));
        model.addAttribute("errorMessage", error.get("field"));
        return "error/404";
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404"; //404.html로 이동.
    }



}
