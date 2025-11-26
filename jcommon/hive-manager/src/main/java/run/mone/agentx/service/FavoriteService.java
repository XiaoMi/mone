package run.mone.agentx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.agentx.dto.FavoriteRequest;
import run.mone.agentx.entity.Favorite;
import run.mone.agentx.dto.enums.FavoriteType;
import run.mone.agentx.repository.FavoriteRepository;
import reactor.core.publisher.Mono;
import run.mone.agentx.dto.common.ListResult;

@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    public Mono<Favorite> addFavorite(FavoriteRequest request) {
        // 验证类型是否有效
        FavoriteType.fromCode(request.getType());
        
        return favoriteRepository.findByUserIdAndTargetIdAndType(
            request.getUserId(), request.getTargetId(), request.getType())
            .flatMap(existingFavorite -> {
                existingFavorite.setState(1);
                existingFavorite.setUtime(System.currentTimeMillis());
                return favoriteRepository.save(existingFavorite);
            })
            .switchIfEmpty(Mono.<Favorite>defer(() -> {
                Favorite favorite = new Favorite();
                favorite.setUserId(request.getUserId());
                favorite.setTargetId(request.getTargetId());
                favorite.setType(request.getType());
                favorite.setState(1);
                favorite.setCtime(System.currentTimeMillis());
                favorite.setUtime(System.currentTimeMillis());
                return favoriteRepository.save(favorite);
            }));
    }
    
    public Mono<Void> removeFavorite(FavoriteRequest request) {
        // 验证类型是否有效
        FavoriteType.fromCode(request.getType());
        
        return favoriteRepository.findByUserIdAndTargetIdAndType(
            request.getUserId(), request.getTargetId(), request.getType())
            .flatMap(favorite -> favoriteRepository.delete(favorite))
            .then();
    }
    
    public Mono<ListResult<Favorite>> getUserFavorites(Integer userId, Integer type) {
        // 验证类型是否有效
        FavoriteType.fromCode(type);
        
        return favoriteRepository.findByUserIdAndType(userId, type)
            .collectList()
            .map(favorites -> {
                ListResult<Favorite> result = new ListResult<>();
                result.setList(favorites);
                result.setPage(1);
                result.setPageSize(favorites.size());
                result.setTotalPage(1);
                return result;
            });
    }
    
    public Mono<Boolean> isFavorite(FavoriteRequest request) {
        // 验证类型是否有效
        FavoriteType.fromCode(request.getType());
        
        return favoriteRepository.findByUserIdAndTargetIdAndType(
            request.getUserId(), request.getTargetId(), request.getType())
            .map(favorite -> true)
            .defaultIfEmpty(false);
    }
} 