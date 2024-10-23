package project.api.rest.numbers.fact.cache;

import java.nio.file.Path;

public interface CacheSystem {

    void loadData(Path file);

    void saveData(Path file);

}
