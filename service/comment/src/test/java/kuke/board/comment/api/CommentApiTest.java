package kuke.board.comment.api;

import kuke.board.comment.response.CommentPageResponse;
import kuke.board.comment.response.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;

public class CommentApiTest {
    RestClient restClient = RestClient.create("http://localhost:9001");

    @Test
    void create() {
        CommentResponse response1 = createComment(new CommentCreateRequest(1L, "content1", null, 1L));
        CommentResponse response2 = createComment(new CommentCreateRequest(1L, "content2", response1.getParentCommentId(), 1L));
        CommentResponse response3 = createComment(new CommentCreateRequest(1L, "content3", response1.getParentCommentId(), 1L));

        System.out.println("response1.getCommentId() = " + response1.getCommentId());
        System.out.println("    response2.getCommentId() = " + response2.getCommentId());
        System.out.println("    response3.getCommentId() = " + response3.getCommentId());
    }

    CommentResponse createComment(CommentCreateRequest request) {
        return restClient.post()
                .uri("/v1/comments")
                .body(request)
                .retrieve()
                .body(CommentResponse.class);
    }

    @Test
    void read() {
        CommentResponse response = restClient.get()
                .uri("/v1/comments/{commentId}", 233124293594726400L)
                .retrieve()
                .body(CommentResponse.class);

        System.out.println("response = " + response);
    }

    @Test
    void delete() {
        restClient.delete()
                .uri("/v1/comments/{commentId}", 233124293594726400L)
                .retrieve();
    }

    @Test
    void readAll() {
        CommentPageResponse response = restClient.get()
                .uri("/v1/comments?articleId={articleId}&page={page}&pageSize={pageSize}", 1L, 1L, 10)
                .retrieve()
                .body(CommentPageResponse.class);

        System.out.println("response.getCommentCount() = " + response.getCommentCount());
        for (CommentResponse comment : response.getComments()) {
            if (!comment.getCommentId().equals(comment.getParentCommentId())) {
                System.out.println();
            }
            System.out.println("comment.getCommentId() = " + comment.getCommentId());
        }
    }

    @Test
    void readAllInfinitEScroll() {
        List<CommentResponse> responses1 = restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId={articleId}&pageSize={pageSize}", 1L, 10L)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("firstPage");
        for (CommentResponse response : responses1) {
            if (!response.getCommentId().equals(response.getParentCommentId())) {
                System.out.println();
            }
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }

        Long lastParentCommentId = responses1.getLast().getParentCommentId();
        Long lastCommentId = responses1.getLast().getCommentId();

        restClient.get()
                .uri("/v1/comments/infinite-scroll?articleId={articleId}&lastParentCommentId={lastParentCommentId}&lastCommentId={lastCommentId}&pageSize={pageSize}", 1L, lastParentCommentId, lastCommentId, 10)
                .retrieve()
                .body(new ParameterizedTypeReference<List<CommentResponse>>() {
                });

        System.out.println("secondPage");
        for (CommentResponse response : responses1) {
            if (!response.getCommentId().equals(response.getParentCommentId())) {
                System.out.println();
            }
            System.out.println("response.getCommentId() = " + response.getCommentId());
        }
    }

    @Getter
    @AllArgsConstructor
    public class CommentCreateRequest {
        private Long articleId;
        private String content;
        private Long parentCommentId;
        private Long writerId;
    }

}
