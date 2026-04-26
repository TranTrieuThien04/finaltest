import { useCallback, useEffect, useMemo, useState } from "react";
import { Plus, Search, Filter, Edit, Trash2, Copy } from "lucide-react";
import { Button } from "../components/ui/button";
import { Input } from "../components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "../components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "../components/ui/select";
import { Badge } from "../components/ui/badge";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "../components/ui/dialog";
import { Label } from "../components/ui/label";
import { Textarea } from "../components/ui/textarea";
import { toast } from "sonner";
import { Alert, AlertDescription, AlertTitle } from "../components/ui/alert";
import { getApiErrorMessage } from "../lib/api";
import { deleteQuestion, listQuestions } from "../lib/questionsApi";
import type { QuestionResponseDto } from "../types/question";

interface Question {
  id: string;
  question: string;
  topic: string;
  difficulty: "Easy" | "Medium" | "Hard";
  type: "Multiple Choice" | "Short Answer" | "Essay";
  answer: string;
  options?: string[];
  createdAt: string;
  /** Duplicated rows only exist in the UI until you create them via the API. */
  isLocalOnly?: boolean;
}

function mapDifficulty(d: QuestionResponseDto["difficulty"]): Question["difficulty"] {
  switch (d) {
    case "EASY":
      return "Easy";
    case "MEDIUM":
      return "Medium";
    case "HARD":
      return "Hard";
    default:
      return "Medium";
  }
}

function mapType(t: QuestionResponseDto["type"]): Question["type"] {
  switch (t) {
    case "MCQ":
      return "Multiple Choice";
    case "FILL_BLANK":
      return "Short Answer";
    case "SHORT_ANSWER":
      return "Essay";
    default:
      return "Short Answer";
  }
}

function mapDtoToQuestion(q: QuestionResponseDto): Question {
  const correct = q.choices?.find((c) => c.correct);
  return {
    id: String(q.questionId),
    question: q.content,
    topic: q.topicName || "—",
    difficulty: mapDifficulty(q.difficulty),
    type: mapType(q.type),
    answer: correct?.content ?? "—",
    options: q.type === "MCQ" ? q.choices.map((c) => c.content) : undefined,
    createdAt: q.createdAt ? String(q.createdAt).slice(0, 10) : "—",
    isLocalOnly: false,
  };
}

export default function QuestionBank() {
  const [questions, setQuestions] = useState<Question[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchTerm, setSearchTerm] = useState("");
  const [filterTopic, setFilterTopic] = useState("all");
  const [filterDifficulty, setFilterDifficulty] = useState("all");
  const [isAddDialogOpen, setIsAddDialogOpen] = useState(false);

  const loadQuestions = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const rows = await listQuestions();
      setQuestions(rows.map(mapDtoToQuestion));
    } catch (e) {
      setError(getApiErrorMessage(e, "Could not load questions"));
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadQuestions();
  }, [loadQuestions]);

  const filteredQuestions = questions.filter((q) => {
    const matchesSearch = q.question.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesTopic = filterTopic === "all" || q.topic === filterTopic;
    const matchesDifficulty = filterDifficulty === "all" || q.difficulty === filterDifficulty;
    return matchesSearch && matchesTopic && matchesDifficulty;
  });

  const handleDeleteQuestion = async (id: string, isLocalOnly?: boolean) => {
    if (isLocalOnly || id.startsWith("local-")) {
      setQuestions((prev) => prev.filter((q) => q.id !== id));
      toast.success("Question removed");
      return;
    }
    try {
      await deleteQuestion(Number(id));
      setQuestions((prev) => prev.filter((q) => q.id !== id));
      toast.success("Question deleted successfully");
    } catch (e) {
      toast.error(getApiErrorMessage(e, "Delete failed"));
    }
  };

  const handleDuplicateQuestion = (question: Question) => {
    const copy: Question = {
      ...question,
      id: `local-${Date.now()}`,
      createdAt: new Date().toISOString().slice(0, 10),
      isLocalOnly: true,
    };
    setQuestions((prev) => [copy, ...prev]);
    toast.success("Local copy added (not saved to the server yet)");
  };

  const topics = useMemo(() => Array.from(new Set(questions.map((q) => q.topic))), [questions]);

  const difficultyColors = {
    Easy: "bg-green-50 text-green-700 border-green-200",
    Medium: "bg-yellow-50 text-yellow-700 border-yellow-200",
    Hard: "bg-red-50 text-red-700 border-red-200",
  };

  if (loading && questions.length === 0 && !error) {
    return (
      <div className="flex min-h-[240px] items-center justify-center text-gray-600">
        Loading questions…
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {error && (
        <Alert variant="destructive">
          <AlertTitle>Could not load questions</AlertTitle>
          <AlertDescription className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
            <span>{error}</span>
            <Button type="button" variant="outline" size="sm" onClick={() => void loadQuestions()}>
              Retry
            </Button>
          </AlertDescription>
        </Alert>
      )}

      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-3xl font-bold text-gray-900">Question Bank</h1>
          <p className="mt-1 text-gray-600">Manage and organize your chemistry questions</p>
        </div>
        <Dialog open={isAddDialogOpen} onOpenChange={setIsAddDialogOpen}>
          <DialogTrigger asChild>
            <Button className="bg-indigo-600 hover:bg-indigo-700">
              <Plus className="mr-2 h-4 w-4" />
              Add Question
            </Button>
          </DialogTrigger>
          <DialogContent className="max-w-2xl">
            <DialogHeader>
              <DialogTitle>Add New Question</DialogTitle>
              <DialogDescription>Create a new question for your question bank</DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div className="space-y-2">
                  <Label>Topic</Label>
                  <Select>
                    <SelectTrigger>
                      <SelectValue placeholder="Select topic" />
                    </SelectTrigger>
                    <SelectContent>
                      {topics.map((topic) => (
                        <SelectItem key={topic} value={topic}>
                          {topic}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-2">
                  <Label>Difficulty</Label>
                  <Select>
                    <SelectTrigger>
                      <SelectValue placeholder="Select difficulty" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="Easy">Easy</SelectItem>
                      <SelectItem value="Medium">Medium</SelectItem>
                      <SelectItem value="Hard">Hard</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
              </div>
              <div className="space-y-2">
                <Label>Question Type</Label>
                <Select>
                  <SelectTrigger>
                    <SelectValue placeholder="Select type" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="Multiple Choice">Multiple Choice</SelectItem>
                    <SelectItem value="Short Answer">Short Answer</SelectItem>
                    <SelectItem value="Essay">Essay</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-2">
                <Label>Question</Label>
                <Textarea placeholder="Enter your question here..." rows={4} />
              </div>
              <div className="space-y-2">
                <Label>Answer</Label>
                <Textarea placeholder="Enter the correct answer..." rows={3} />
              </div>
            </div>
            <DialogFooter>
              <Button variant="outline" onClick={() => setIsAddDialogOpen(false)}>
                Cancel
              </Button>
              <Button
                className="bg-indigo-600 hover:bg-indigo-700"
                onClick={() => {
                  setIsAddDialogOpen(false);
                  toast.success("Question added successfully");
                }}
              >
                Add Question
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="p-6">
          <div className="grid gap-4 md:grid-cols-3">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-400" />
              <Input
                placeholder="Search questions..."
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="pl-9"
              />
            </div>
            <Select value={filterTopic} onValueChange={setFilterTopic}>
              <SelectTrigger>
                <div className="flex items-center gap-2">
                  <Filter className="h-4 w-4" />
                  <SelectValue placeholder="All Topics" />
                </div>
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Topics</SelectItem>
                {topics.map((topic) => (
                  <SelectItem key={topic} value={topic}>
                    {topic}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
            <Select value={filterDifficulty} onValueChange={setFilterDifficulty}>
              <SelectTrigger>
                <SelectValue placeholder="All Difficulties" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="all">All Difficulties</SelectItem>
                <SelectItem value="Easy">Easy</SelectItem>
                <SelectItem value="Medium">Medium</SelectItem>
                <SelectItem value="Hard">Hard</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </CardContent>
      </Card>

      {/* Stats */}
      <div className="grid gap-4 md:grid-cols-4">
        <Card>
          <CardContent className="p-4">
            <p className="text-sm text-gray-600">Total Questions</p>
            <p className="text-2xl font-bold text-gray-900">{questions.length}</p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <p className="text-sm text-gray-600">Easy</p>
            <p className="text-2xl font-bold text-green-600">
              {questions.filter((q) => q.difficulty === "Easy").length}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <p className="text-sm text-gray-600">Medium</p>
            <p className="text-2xl font-bold text-yellow-600">
              {questions.filter((q) => q.difficulty === "Medium").length}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="p-4">
            <p className="text-sm text-gray-600">Hard</p>
            <p className="text-2xl font-bold text-red-600">
              {questions.filter((q) => q.difficulty === "Hard").length}
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Questions List */}
      {!loading && questions.length === 0 && !error ? (
        <Card>
          <CardHeader>
            <CardTitle>No questions yet</CardTitle>
          </CardHeader>
          <CardContent className="text-sm text-gray-600">
            Create questions in the backend or extend this page to call{" "}
            <code className="rounded bg-gray-100 px-1">POST /api/v1/questions</code>.
          </CardContent>
        </Card>
      ) : (
        <div className="space-y-4">
          {filteredQuestions.map((question) => (
            <Card key={question.id} className="transition-shadow hover:shadow-md">
              <CardContent className="p-6">
                <div className="flex items-start justify-between gap-4">
                  <div className="flex-1">
                    <div className="mb-2 flex items-center gap-2">
                      <Badge variant="outline" className="border-indigo-200 text-indigo-600">
                        {question.topic}
                      </Badge>
                      <Badge variant="outline" className={difficultyColors[question.difficulty]}>
                        {question.difficulty}
                      </Badge>
                      <Badge variant="outline" className="text-gray-600">
                        {question.type}
                      </Badge>
                    </div>
                    <h3 className="mb-2 text-lg font-semibold text-gray-900">{question.question}</h3>
                    {question.options && (
                      <div className="mt-3 grid grid-cols-2 gap-2">
                        {question.options.map((option, idx) => (
                          <div
                            key={idx}
                            className={`rounded-lg border p-2 text-sm ${
                              option === question.answer
                                ? "border-green-500 bg-green-50 text-green-900"
                                : "border-gray-200 bg-gray-50 text-gray-700"
                            }`}
                          >
                            {String.fromCharCode(65 + idx)}. {option}
                          </div>
                        ))}
                      </div>
                    )}
                    <p className="mt-3 text-xs text-gray-500">Created: {question.createdAt}</p>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="ghost" size="icon" onClick={() => handleDuplicateQuestion(question)}>
                      <Copy className="h-4 w-4" />
                    </Button>
                    <Button variant="ghost" size="icon">
                      <Edit className="h-4 w-4" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      onClick={() => void handleDeleteQuestion(question.id, question.isLocalOnly)}
                    >
                      <Trash2 className="h-4 w-4 text-red-600" />
                    </Button>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
}
