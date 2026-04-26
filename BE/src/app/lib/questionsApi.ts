import type { QuestionResponseDto } from '../types/question';
import { api } from './api';

// === TYPES ===
export interface TopicDto {
  topicId: number;
  name: string;
  subjectId: number;
}

export interface SubjectDto {
  subjectId: number;
  name: string;
}

export interface QuestionCreateDto {
  content: string;
  type: 'MCQ' | 'FILL_BLANK' | 'SHORT_ANSWER';
  difficulty: 'EASY' | 'MEDIUM' | 'HARD';
  topicId: number;
  choices: { content: string; correct: boolean }[];
}

// === API CALLS ===
export async function listQuestions(params?: {
  topicId?: number;
  subjectId?: number;
}): Promise<QuestionResponseDto[]> {
  const { data } = await api.get<QuestionResponseDto[]>('/api/v1/questions', { params });
  return data;
}

export async function createQuestion(body: QuestionCreateDto): Promise<QuestionResponseDto> {
  const { data } = await api.post<QuestionResponseDto>('/api/v1/questions', body);
  return data;
}

export async function deleteQuestion(questionId: number): Promise<void> {
  await api.delete(`/api/v1/questions/${questionId}`);
}

export async function listTopics(): Promise<TopicDto[]> {
  const { data } = await api.get<TopicDto[]>('/api/v1/topics');
  return data;
}

export async function listSubjects(): Promise<SubjectDto[]> {
  const { data } = await api.get<SubjectDto[]>('/api/v1/subjects');
  return data;
}