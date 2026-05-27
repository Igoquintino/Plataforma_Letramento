package com.projeto.tcc.letramento.contract;

import com.projeto.tcc.letramento.controller.ScenarioController;
import com.projeto.tcc.letramento.enums.CidPillar;
import com.projeto.tcc.letramento.model.Scenario;
import com.projeto.tcc.letramento.security.JwtTokenProvider;
import com.projeto.tcc.letramento.service.ProgressService;
import com.projeto.tcc.letramento.service.ScenarioService;
import com.projeto.tcc.letramento.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ScenarioController.class)
class ScenarioContractTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private ScenarioService scenarioService;
    @MockitoBean private ProgressService progressService;
    @MockitoBean private UserService userService;
    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("CONTRATO — O payload do ScenarioDTO deve manter estritamente as chaves e tipos esperados pelo React")
    void validateScenarioXRay_PayloadContract() throws Exception {
        Long scenarioId = 1L;

        Scenario mockScenario = new Scenario();
        mockScenario.setId(scenarioId);
        mockScenario.setTitleScenarios("Simulação de Token JWT Exposto");
        mockScenario.setPillar(CidPillar.CONFIDENCIALIDADE);
        mockScenario.setXrayData(mapper.createObjectNode().put("vulnerability", "Signature Bypass"));

        when(scenarioService.getScenarioWithXray(scenarioId)).thenReturn(mockScenario);

        // Executa a chamada fake simulando o consumo do frontend
        mockMvc.perform(get("/api/scenarios/" + scenarioId + "/xray")
                        .with(oauth2Login())
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())

                // Garante que as chaves JSON existem EXATAMENTE com esses nomes e tipos
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").isString())
                .andExpect(jsonPath("$.pillar").isString())
                .andExpect(jsonPath("$.xrayData").isMap())

                // Valida o valor exato do mapeamento do Record para conferir integridade
                .andExpect(jsonPath("$.title").value("Simulação de Token JWT Exposto"))
                .andExpect(jsonPath("$.pillar").value("CONFIDENCIALIDADE"));
    }
}