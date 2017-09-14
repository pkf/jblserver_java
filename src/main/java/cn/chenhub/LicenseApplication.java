package cn.chenhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LicenseApplication {
	public static void main(String[] args) {
		for (int i = 0;i<args.length;i++){
			String[] strings = args[i].split("=");
			if("-p".equals(strings[0])){
				args[i]="--server.port="+strings[1];
			}
			if("-log".equals(strings[0])){
				if (strings[1].contains("/")){
					System.out.println("日志文件命名错误，已使用默认log.log");
					args[i]="logging.file=log.log";
				}
				else args[i]="logging.file="+strings[1];
			}
		}
		SpringApplication.run(LicenseApplication.class, args);
	}
}
