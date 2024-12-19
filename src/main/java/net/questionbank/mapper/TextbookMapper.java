package net.questionbank.mapper;

import net.questionbank.dto.textbook.TextbookDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TextbookMapper {
    List<String> authorListBySubject(String subjectId);
    List<TextbookDTO> textbookListsByAuthor(String author);
}
