//package com.example.ranking.global;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RestDocsConfiguration {
//
//    @Bean
//    public RestDocumentationResultHandler restDocumentationResultHandler() {
//        return MockMvcRestDocumentation.document(
//                "{class-name}/{method-name}",
//                preprocessRequest(
//                        modifyHeaders()
//                                .remove("Content-Length")
//                                .remove("Host"),
//                        prettyPrint()
//                ),
//                preprocessResponse(
//                        modifyHeaders()
//                                .remove("Content-Length")
//                                .remove("X-Content-Type-Options")
//                                .remove("X-XSS-Protection")
//                                .remove("Cache-Control")
//                                .remove("Pragma")
//                                .remove("Expires")
//                                .remove("X-Frame-Options"),
//                        prettyPrint()
//                )
//        );
//    }
//
//}
