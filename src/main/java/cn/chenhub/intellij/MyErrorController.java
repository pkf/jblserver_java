package cn.chenhub.intellij;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/error")
public class MyErrorController implements ErrorController {
    @Override
    public String getErrorPath() {
        return null;
    }
}
