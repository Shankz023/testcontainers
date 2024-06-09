package dev.shankz.testcontainers.post;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;


@Component
public class PostDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PostDataLoader.class);
    private final ObjectMapper objectMapper;
    private final PostRepository postRepository;

    public PostDataLoader(ObjectMapper objectMapper, PostRepository postRepository) {
        this.objectMapper = objectMapper;
        this.postRepository = postRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (postRepository.count() == 0) {
            String POSTS_JSON = "/data/posts.json";
            log.info("Loading posts into database from JSON: {}", POSTS_JSON);
            try (InputStream is = TypeReference.class.getResourceAsStream(POSTS_JSON)) {
                Posts response = objectMapper.readValue(is, Posts.class);
                postRepository.saveAll(response.posts());
            } catch (IOException ie) {
                throw new RuntimeException("Failed to read JSON data", ie);
            }
        }
    }

}
