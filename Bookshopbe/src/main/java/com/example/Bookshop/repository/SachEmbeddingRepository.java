package com.example.Bookshop.repository;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SachEmbeddingRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String datasourceUrl;

    public SachEmbeddingRepository(
            JdbcTemplate jdbcTemplate,
            @Value("${spring.datasource.url:}") String datasourceUrl) {
        this.jdbcTemplate = jdbcTemplate;
        this.datasourceUrl = datasourceUrl;
    }

    @PostConstruct
    void initializeSchema() {
        if (!isPostgres()) {
            return;
        }
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
        jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS sach_embedding (
                    sach_id INTEGER PRIMARY KEY REFERENCES sach(id) ON DELETE CASCADE,
                    content TEXT NOT NULL,
                    embedding vector(3072) NOT NULL,
                    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                )
                """);
    }

    public void upsert(Integer bookId, String content, String vector) {
        jdbcTemplate.update("""
                INSERT INTO sach_embedding (sach_id, content, embedding, updated_at)
                VALUES (?, ?, CAST(? AS vector), CURRENT_TIMESTAMP)
                ON CONFLICT (sach_id) DO UPDATE SET
                    content = EXCLUDED.content,
                    embedding = EXCLUDED.embedding,
                    updated_at = CURRENT_TIMESTAMP
                """, bookId, content, vector);
    }

    public List<Integer> findNearestIds(String queryVector, int limit) {
        return jdbcTemplate.queryForList("""
                SELECT sach_id
                FROM sach_embedding
                ORDER BY embedding <=> CAST(? AS vector)
                LIMIT ?
                """, Integer.class, queryVector, Math.max(1, limit));
    }

    public boolean isEmpty() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM sach_embedding", Integer.class);
        return count == null || count == 0;
    }

    private boolean isPostgres() {
        return datasourceUrl.startsWith("jdbc:postgresql:");
    }
}
