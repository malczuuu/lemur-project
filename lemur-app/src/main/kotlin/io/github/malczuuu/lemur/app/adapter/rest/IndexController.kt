package io.github.malczuuu.lemur.app.adapter.rest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping(path = ["/"])
class IndexController {

    @GetMapping
    fun get(): String = "redirect:/swagger-ui/index.html"
}
