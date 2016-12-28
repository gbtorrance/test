package org.tdc.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.tdc.book.Book;
import org.tdc.book.TestCase;
import org.tdc.book.TestDoc;
import org.tdc.book.TestSet;
import org.tdc.result.Message;
import org.tdc.result.Result;
import org.tdc.result.Results;
import org.tdc.result.TaskResult;
import org.tdc.server.dto.BookDTO;
import org.tdc.server.dto.MessageDTO;
import org.tdc.server.dto.ResultDTO;
import org.tdc.server.dto.ResultsDTO;
import org.tdc.server.dto.TaskResultDTO;
import org.tdc.server.dto.TestCaseDTO;
import org.tdc.server.dto.TestDocDTO;
import org.tdc.server.dto.TestSetDTO;

/**
 * MapStruct Mapper to map {@link Book} (and related) to {@link BookDTO} (and related).
 */
@Mapper(uses = UtilMapper.class)
public abstract class BookMapper {
	public static BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

	@Mapping(source = "config.addr", target = "bookAddress")
	public abstract BookDTO bookToBookDTO(Book book);
	
	public abstract TestSetDTO testSetToTestSetDTO(TestSet testSet);

	public abstract TestCaseDTO testCaseToTestCaseDTO(TestCase testCase);
	
	@Mapping(source = "pageConfig.pageName", target = "pageName")
	@Mapping(source = "pageConfig.docTypeConfig.docTypeName", target = "docTypeName")
	public abstract TestDocDTO testDocToTestDocDTO(TestDoc testDoc);
	
	public abstract ResultsDTO resultsToResultsDTO(Results results);
	
	public abstract ResultDTO resultToResultDTO(Result result);
	
	public abstract TaskResultDTO taskResultToTaskResultDTO(TaskResult taskResult);
	
	public abstract MessageDTO messageToMessageDTO(Message message);

//  TODO sample of how filtering of lists can be done; 
//       possibly pass in a Filter object to the mapper constructor
//	public List<TestCaseDTO> testCaseFilter(List<TestCase> testCases) {
//		List<TestCaseDTO> dtos = new ArrayList<>();
//		for (int i = 0; i < Math.min(testCases.size(), 5); i++) {
//			TestCaseDTO dto = testCaseToTestCaseDTO(testCases.get(i)); 
//			dtos.add(dto);
//		}
//		return dtos;
//	}
}
