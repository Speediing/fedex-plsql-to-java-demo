package com.fedexdemo.rating.web;

import java.util.List;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScenariosController {

    @GetMapping("/scenarios")
    public List<Map<String, String>> scenarios() throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:scenarios/*.json");
        return java.util.Arrays.stream(resources)
                .map(resource -> Map.of(
                        "id", resource.getFilename().replace(".json", ""),
                        "path", "/scenarios/" + resource.getFilename()))
                .toList();
    }

    @GetMapping(value = "/scenarios/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Resource scenarioFile(@org.springframework.web.bind.annotation.PathVariable String name)
            throws Exception {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResource("classpath:scenarios/" + name);
    }
}
