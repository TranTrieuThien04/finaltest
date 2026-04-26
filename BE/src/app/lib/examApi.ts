import { api } from './api';

export interface ExamCreateDto {
  title: string;
  duration: number;         // phút
  totalQuestions: number;
  topicId?: number;         // null = lấy từ tất cả topic
}

export interface ExamResponseDto {
  examId: number;
  title: string;
  duration: number;
  totalQuestions: number;
  teacherId: number;
  createdAt: string;
  questionIds: number[];
}

export async function createExam(body: ExamCreateDto): Promise<ExamResponseDto> {
  const { data } = await api.post<ExamResponseDto>('/api/v1/exams', body);
  return data;
}

export async function listExams(): Promise<ExamResponseDto[]> {
  const { data } = await api.get<ExamResponseDto[]>('/api/v1/exams');
  return data;
}

export async function getExam(id: number): Promise<ExamResponseDto> {
  const { data } = await api.get<ExamResponseDto>(`/api/v1/exams/${id}`);
  return data;
}

export async function deleteExam(id: number): Promise<void> {
  await api.delete(`/api/v1/exams/${id}`);
}