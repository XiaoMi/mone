package run.mone.agentx.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.entity.Skill;
import run.mone.agentx.entity.User;
import run.mone.agentx.service.SkillService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping("/agents/{agentId}/skills")
    public Mono<ApiResponse<Skill>> createSkill(@AuthenticationPrincipal User user, @PathVariable Long agentId, @RequestBody Skill skill) {
        skill.setAgentId(agentId);
        return skillService.createSkill(skill).map(ApiResponse::success);
    }

    @GetMapping("/agents/{agentId}/skills")
    public Mono<ApiResponse<List<Skill>>> getSkills(@AuthenticationPrincipal User user, @PathVariable Long agentId) {
        return skillService.findByAgentId(agentId).collectList().map(ApiResponse::success);
    }

    @GetMapping("/skills/{id}")
    public Mono<ApiResponse<Skill>> getSkill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return skillService.findById(id).map(ApiResponse::success);
    }

    @PutMapping("/skills/{id}")
    public Mono<ApiResponse<Skill>> updateSkill(@AuthenticationPrincipal User user, @PathVariable Long id, @RequestBody Skill skill) {
        skill.setId(id);
        return skillService.updateSkill(skill).map(ApiResponse::success);
    }

    @DeleteMapping("/skills/{id}")
    public Mono<ApiResponse<Void>> deleteSkill(@AuthenticationPrincipal User user, @PathVariable Long id) {
        return skillService.deleteSkill(id).thenReturn(ApiResponse.success(null));
    }
}