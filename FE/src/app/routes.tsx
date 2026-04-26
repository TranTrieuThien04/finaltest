import { createBrowserRouter } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import ExerciseGenerator from "./pages/ExerciseGenerator";
import OCRGrading from "./pages/OCRGrading";
import DashboardLayout from "./components/DashboardLayout"; // ĐÃ SỬA: Dùng ./ thay vì ../
import GenericManager from "./pages/GenericManager";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <LoginPage />,
  },
  {
    path: "/login",
    element: <LoginPage />,
  },
  {
    path: "/teacher",
    element: <DashboardLayout />,
    children: [
      { index: true, element: <GenericManager title="Dashboard Thống kê" endpoint="/dashboard" /> },
      { path: "question-bank", element: <GenericManager title="Ngân hàng câu hỏi" endpoint="/questions" /> },
      { path: "exercise-generator", element: <ExerciseGenerator /> },
      { path: "exam-generator", element: <GenericManager title="Quản lý đề thi" endpoint="/teacher-exams" /> },
      { path: "ocr-grading", element: <OCRGrading /> },
      { path: "analytics", element: <GenericManager title="Phân tích dữ liệu" endpoint="/teacher-analytics" /> },
    ],
  },
  {
    path: "/admin",
    element: <DashboardLayout />,
    children: [
      { index: true, element: <GenericManager title="Quản trị User (Admin)" endpoint="/admin-users" /> },
    ],
  },
  {
    path: "/manager",
    element: <DashboardLayout />,
    children: [
      { index: true, element: <GenericManager title="Quản lý Đơn hàng" endpoint="/manager-orders" /> },
    ],
  },
  {
    path: "/staff",
    element: <DashboardLayout />,
    children: [
      { index: true, element: <GenericManager title="Quản lý Prompt Staff" endpoint="/prompt-staff" /> },
    ],
  },
]);