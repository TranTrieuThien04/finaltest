// THAY THẾ TOÀN BỘ routes.tsx:
import { createBrowserRouter, Navigate } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import DashboardLayout from "./components/DashboardLayout";
import ProtectedRoute from "./components/ProtectedRoute";
import TeacherDashboard from "./pages/TeacherDashboard";
import QuestionBank from "./pages/QuestionBank";
import ExamGenerator from "./pages/ExamGenerator";
import ExerciseGenerator from "./pages/ExerciseGenerator";
import OCRGrading from "./pages/OCRGrading";
import AdminUserManagement from "./pages/AdminUserManagement";
import ManagerPackages from "./pages/ManagerPackages";
import ManagerOrders from "./pages/ManagerOrders";
import ManagerApproval from "./pages/ManagerApproval";
import StaffPrompts from "./pages/StaffPrompts";
import StaffLessonPlans from "./pages/StaffLessonPlans";

export const router = createBrowserRouter([
  { path: "/", element: <Navigate to="/login" /> },
  { path: "/login", element: <LoginPage /> },
  {
    path: "/teacher",
    element: <ProtectedRoute allowedRoles={["teacher","admin"]}><DashboardLayout /></ProtectedRoute>,
    children: [
      { index: true,                   element: <TeacherDashboard /> },
      { path: "question-bank",         element: <QuestionBank /> },
      { path: "exam-generator",        element: <ExamGenerator /> },
      { path: "exercise-generator",    element: <ExerciseGenerator /> },
      { path: "ocr-grading",           element: <OCRGrading /> },
    ],
  },
  {
    path: "/admin",
    element: <ProtectedRoute allowedRoles={["admin"]}><DashboardLayout /></ProtectedRoute>,
    children: [
      { index: true,   element: <AdminUserManagement /> },
      { path: "users", element: <AdminUserManagement /> },
    ],
  },
  {
    path: "/manager",
    element: <ProtectedRoute allowedRoles={["manager","admin"]}><DashboardLayout /></ProtectedRoute>,
    children: [
      { index: true,          element: <ManagerOrders /> },
      { path: "packages",     element: <ManagerPackages /> },
      { path: "orders",       element: <ManagerOrders /> },
      { path: "approval",     element: <ManagerApproval /> },
    ],
  },
  {
    path: "/staff",
    element: <ProtectedRoute allowedRoles={["staff","admin"]}><DashboardLayout /></ProtectedRoute>,
    children: [
      { index: true,          element: <StaffLessonPlans /> },
      { path: "lesson-plans", element: <StaffLessonPlans /> },
      { path: "prompts",      element: <StaffPrompts /> },
    ],
  },
  { path: "*", element: <Navigate to="/login" /> },
]);