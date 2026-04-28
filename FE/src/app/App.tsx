import { RouterProvider } from "react-router-dom";
import { router } from "./routes"; // Kiểm tra lại đường dẫn này cho đúng file routes.tsx
import { Toaster } from "sonner";

function App() {
  return (
    <>
      {/* Cấp quyền định tuyến cho toàn bộ ứng dụng */}
      <RouterProvider router={router} />
      
      {/* Hiển thị thông báo toast */}
      <Toaster position="top-right" richColors />
    </>
  );
}

export default App;