package com.easygame.api.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "filter-paths")
public class FilterPathsProperties {
    private AuthPath authPath;
    private SubmitPath submitPath;

    @Getter
    @Setter
    public static class AuthPath {
        private List<String> includePaths;

        public List<String> getIncludePaths() {
            return includePaths;
        }

        public void setIncludePaths(List<String> includePaths) {
            this.includePaths = includePaths;
        }
    }

    @Getter
    @Setter
    public static class SubmitPath {
        private List<String> includePaths;

        public List<String> getIncludePaths() {
            return includePaths;
        }

        public void setIncludePaths(List<String> includePaths) {
            this.includePaths = includePaths;
        }
    }
}
