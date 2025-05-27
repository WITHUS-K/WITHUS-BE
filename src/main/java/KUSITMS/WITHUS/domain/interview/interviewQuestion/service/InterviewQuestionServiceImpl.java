package KUSITMS.WITHUS.domain.interview.interviewQuestion.service;

import KUSITMS.WITHUS.domain.application.application.entity.Application;
import KUSITMS.WITHUS.domain.application.application.repository.ApplicationRepository;
import KUSITMS.WITHUS.domain.interview.interviewQuestion.entity.InterviewQuestion;
import KUSITMS.WITHUS.domain.interview.interviewQuestion.repository.InterviewQuestionRepository;
import KUSITMS.WITHUS.domain.user.user.entity.User;
import KUSITMS.WITHUS.domain.user.user.repository.UserRepository;
import KUSITMS.WITHUS.global.exception.CustomException;
import KUSITMS.WITHUS.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InterviewQuestionServiceImpl implements InterviewQuestionService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    @Override
    @Transactional
    public InterviewQuestion addQuestionToApplication(Long applicationId, Long userId, String content) {
        Application application = applicationRepository.getById(applicationId);
        User user = userRepository.getById(userId);

        InterviewQuestion question = InterviewQuestion.builder()
                .content(content)
                .application(application)
                .user(user)
                .build();

        interviewQuestionRepository.save(question);
        application.addInterviewQuestion(question);

        return question;
    }

    @Override
    @Transactional
    public InterviewQuestion updateQuestion(Long questionId, Long id, String content) {
        InterviewQuestion question = interviewQuestionRepository.getById(questionId);

        if (!question.getUser().getId().equals(id)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }

        question.updateContent(content);

        return question;
    }
}
