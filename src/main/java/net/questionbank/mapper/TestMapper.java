package net.questionbank.mapper;

import net.questionbank.dto.test.TestDTO;
import net.questionbank.dto.test.TestSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TestMapper {
    int updateTitle(String title, int testId);
    List<TestDTO> findTestList(TestSearchDTO testSearchDTO);
}
