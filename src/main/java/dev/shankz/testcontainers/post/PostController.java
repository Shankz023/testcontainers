package dev.shankz.testcontainers.post;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/posts")
class PostController {

    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    private final PostRepository repository;

    public PostController(PostRepository repository) {
        this.repository = repository;
    }

    @GetMapping("")
    List<Post> findAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    Optional<Post> findById(@PathVariable Integer id) {
        return Optional.ofNullable(repository.findById(id).orElseThrow(PostNotFoundException::new));
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    Post save(@RequestBody @Valid Post post) {
        log.info("Creating Post");
        return repository.save(post);
    }

    @PutMapping("/{id}")
    @Transactional
    Post update(@PathVariable Integer id, @RequestBody Post post) {
        Optional<Post> existing = repository.findById(id);
        if (existing.isPresent()) {
            Post updatedPost = new Post(existing.get().id(),
                    existing.get().userId(),
                    post.title(),
                    post.body(),
                    existing.get().version());

            return repository.save(updatedPost);
        } else {
            throw new PostNotFoundException();
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    @Transactional
    void delete(@PathVariable Integer id) {
        repository.deleteById(id);
    }

}
