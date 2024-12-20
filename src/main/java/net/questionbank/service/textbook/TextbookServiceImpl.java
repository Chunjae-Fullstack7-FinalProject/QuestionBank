package net.questionbank.service.textbook;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.questionbank.dto.main.MainDTO;
import net.questionbank.dto.textbook.TextbookDTO;
import net.questionbank.mapper.TextbookMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TextbookServiceImpl implements TextbookServiceIf{
    private final TextbookMapper textbookMapper;

    @Override
    public List<MainDTO> mainList(String subjectId) {
        List<String> authorList = textbookMapper.authorListBySubject(subjectId);
        List<MainDTO> mainList = new ArrayList<>();
        for(String author : authorList) {
            MainDTO mainDTO = new MainDTO();
            List<TextbookDTO> textbooks = textbookMapper.textbookListsByAuthor(author);
            mainDTO.setAuthorName(author);
            mainDTO.setTextbooks(textbooks);
            mainList.add(mainDTO);
        }
        return mainList;
    }
}
