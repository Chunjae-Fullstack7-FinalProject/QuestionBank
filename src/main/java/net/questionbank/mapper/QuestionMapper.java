package net.questionbank.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapper {
    List<Long> findItemIdByTestId(int testId);
}
