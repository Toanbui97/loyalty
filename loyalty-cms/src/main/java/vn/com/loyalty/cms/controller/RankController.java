package vn.com.loyalty.cms.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import vn.com.loyalty.core.dto.request.BodyRequest;
import vn.com.loyalty.core.dto.request.RankRequest;
import vn.com.loyalty.core.dto.response.cms.RankResponse;
import vn.com.loyalty.core.service.internal.impl.RankService;
import vn.com.loyalty.core.utils.factory.response.BodyResponse;
import vn.com.loyalty.core.utils.factory.response.ResponseFactory;

@RestController
@RequestMapping(value = "/api/v1", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RankController {

    private final RankService rankService;
    private final ResponseFactory responseFactory;

    @PostMapping("/syncRankToRedis")
    public ResponseEntity<BodyResponse<RankResponse>> syncRankToRedis(@Nullable @RequestBody BodyRequest<RankRequest> request) {
        return responseFactory.success(rankService.syncRankWithRedis());
    }

    @PostMapping("/receiveRankList")
    public ResponseEntity<BodyResponse<RankResponse>> receiveRankList(@Nullable @RequestBody BodyRequest<RankRequest> request, Pageable pageable) {
        return responseFactory.success(rankService.getRankList(request.getData()));
    }

    @PostMapping("/performCreateRank")
    public ResponseEntity<BodyResponse<RankResponse>> performCreateRank(@RequestBody BodyRequest<RankRequest> request) {
        return responseFactory.success(rankService.createRank(request.getData()));
    }

    @PostMapping("/performUpdateRank")
    public ResponseEntity<BodyResponse<RankResponse>> performUpdateRank(@RequestBody BodyRequest<RankRequest> request) {
        return responseFactory.success(rankService.updateRank(request.getData()));
    }

    @PostMapping("/receiveRankInfo")
    public ResponseEntity<BodyResponse<RankResponse>> receiveRankInfo(@RequestBody BodyRequest<RankRequest> request) {
        return responseFactory.success(rankService.getRankInform(request.getData()));
    }

    @PostMapping("/performDeleteRank/{rankCode}")
    public ResponseEntity<BodyResponse<RankResponse>> performDeleteRank(@RequestBody BodyRequest<RankRequest> req, @PathVariable String rankCode) {
        return responseFactory.success(rankService.deleteRank(rankCode));
    }

}
