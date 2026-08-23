# Build stage
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app
COPY . .
RUN ./gradlew build -x test

# Runtime stage
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080

# ############################################################################
# 임시: 지원서 제출 부하 테스트(#184) 전용 설정. 절대 develop 에 머지하지 말 것.
#
# 커맨드라인 인자는 프로퍼티 우선순위 최상위라 ConfigMap/환경변수를 모두 덮는다.
# noop 은 메일을 실제로 발송하지 않으므로, 이 상태에서는 지원서 접수 확인은 물론
# 회원가입 인증메일·조직 초대·평가 리마인드가 전부 나가지 않는다.
#
# 테스트 종료 후 이 인자 두 개를 제거하고 재배포해야 메일이 정상화된다.
# ############################################################################
ENTRYPOINT ["java", "-jar", "app.jar", "--mail.provider=noop", "--mail.noop-delay-ms=1500"]
