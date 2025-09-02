package myex.shopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication //내부적으로 @ComponentScan 포함 : 빈 찾아서 등록해줌. : 기본 스캔 경로 : 현재 패키지와 하위 패키지 (shopping 과 그 하위.)
public class ShoppingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoppingApplication.class, args);
	}

}
