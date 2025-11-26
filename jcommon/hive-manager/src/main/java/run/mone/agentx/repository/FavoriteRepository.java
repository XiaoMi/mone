package run.mone.agentx.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import run.mone.agentx.entity.Favorite;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface FavoriteRepository extends ReactiveCrudRepository<Favorite, Long> {
    
    @Query("SELECT * FROM t_favorite WHERE user_id = :userId AND target_id = :targetId AND type = :type")
    Mono<Favorite> findByUserIdAndTargetIdAndType(
        @Param("userId") Integer userId,
        @Param("targetId") Integer targetId,
        @Param("type") Integer type
    );
    
    @Query("SELECT * FROM t_favorite WHERE user_id = :userId AND type = :type")
    Flux<Favorite> findByUserIdAndType(
        @Param("userId") Integer userId,
        @Param("type") Integer type
    );
} 