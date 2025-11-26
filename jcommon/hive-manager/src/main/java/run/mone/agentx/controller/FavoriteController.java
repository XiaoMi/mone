package run.mone.agentx.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.FavoriteRequest;
import run.mone.agentx.entity.Favorite;
import run.mone.agentx.service.FavoriteService;
import run.mone.agentx.dto.common.ApiResponse;
import run.mone.agentx.dto.common.ListResult;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    @PostMapping("/add")
    public Mono<ApiResponse<Favorite>> addFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.addFavorite(request)
            .map(favorite -> ApiResponse.success(favorite));
    }
    
    @PostMapping("/remove")
    public Mono<ApiResponse<Void>> removeFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.removeFavorite(request)
            .then(Mono.just(ApiResponse.success(null)));
    }
    
    @GetMapping("/list")
    public Mono<ApiResponse<ListResult<Favorite>>> getUserFavorites(
        @RequestParam Integer userId,
        @RequestParam Integer type
    ) {
        return favoriteService.getUserFavorites(userId, type)
            .map(ApiResponse::success);
    }
    
    @GetMapping("/check")
    public Mono<ApiResponse<Boolean>> isFavorite(@RequestBody FavoriteRequest request) {
        return favoriteService.isFavorite(request)
            .map(result -> ApiResponse.success(result));
    }
} 