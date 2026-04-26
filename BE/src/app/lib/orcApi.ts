import { api } from './api';

export interface OcrSimulateDto {
  examId: number;
  studentName: string;
}

export interface OcrResultDto {
  ocrResultId: number;
  examId: number;
  studentName: string;
  score: number;
  resultJson: string;
  gradedAt: string;
}

// Simulate OCR grading (backend random score 5-10)
export async function simulateOcr(body: OcrSimulateDto): Promise<OcrResultDto> {
  const { data } = await api.post<OcrResultDto>('/api/v1/ocr/simulate', body);
  return data;
}

export async function getOcrResultsByExam(examId: number): Promise<OcrResultDto[]> {
  const { data } = await api.get<OcrResultDto[]>(`/api/v1/ocr/exam/${examId}`);
  return data;
}