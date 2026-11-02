package com.kaio.limbus_gacha_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI limbusGachaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Limbus Gacha API")
                        .version("1.0.0")
                        .description("""
                                API de jogadores do gacha de Limbus Company, consumida pelo app mobile.
                                Faz login por username, controla Lunacy, pity por banner, inventário e o sorteio.

                                Taxas (Regular Extraction): E.G.O. 1,3% · 000 2,9% · 00 12,8% · 0 83%.
                                Com todos os E.G.O. do banner: 000 3% · 00 13% · 0 84%.
                                A cada 10 giros no mesmo banner, um giro é garantido 00 ou superior.
                                Cada giro custa 130 Lunacy (1300 no 10x).

                                Todo erro responde no formato { "error": "mensagem" }.
                                """));
    }
}
