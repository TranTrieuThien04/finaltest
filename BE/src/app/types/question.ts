/** Mirrors backend `QuestionResponse` JSON shape. */
export type QuestionTypeApi = "MCQ" | "FILL_BLANK" | "SHORT_ANSWER";
export type QuestionDifficultyApi = "EASY" | "MEDIUM" | "HARD";
export type QuestionStatusApi = string;

export interface QuestionChoiceResponseDto {
  questionChoiceId: number;
  content: string;
  correct: boolean;
}

export interface QuestionResponseDto {
  questionId: number;
  topicId: number;
  topicName: string;
  createdByUserId: number;
  content: string;
  type: QuestionTypeApi;
  difficulty: QuestionDifficultyApi;
  status: QuestionStatusApi;
  createdAt: string;
  choices: QuestionChoiceResponseDto[];
}
