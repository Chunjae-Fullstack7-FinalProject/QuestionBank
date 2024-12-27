package net.questionbank.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    int updateTitle(String title, int testId);
}
