import { api } from './api';

export interface ExerciseCreateDto {
  title: string;
  topicId?: number;
  totalQuestions: number;
}

export interface ExerciseResponseDto {
  exerciseId: number;
  title: string;
  teacherId: number;
  createdAt: string;
  questionIds: number[];
}

export async function createExercise(body: ExerciseCreateDto): Promise<ExerciseResponseDto> {
  const { data } = await api.post<ExerciseResponseDto>('/api/v1/exercises', body);
  return data;
}

export async function listExercises(): Promise<ExerciseResponseDto[]> {
  const { data } = await api.get<ExerciseResponseDto[]>('/api/v1/exercises');
  return data;
}