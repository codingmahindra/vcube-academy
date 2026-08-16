import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { careerApi, type DailyPlan } from '../api/career';

import {
  Calendar,
  CheckCircle,
  Circle,
  ArrowRight,
  TrendingUp,
  AlertCircle,
  Sparkles,
  BookOpen,
  Code2,
  Brain,
  FileText,
  FileCheck,
  ShieldCheck,
} from 'lucide-react';

export default function DailyPlanPage() {
  const navigate = useNavigate();

  const [dailyPlan, setDailyPlan] =
    useState<DailyPlan | null>(null);

  const [loading, setLoading] =
    useState<boolean>(true);

  const [error, setError] =
    useState<string | null>(null);

  const [togglingId, setTogglingId] =
    useState<number | null>(null);


  // =====================================================
  // LOAD DAILY PLAN
  // =====================================================

  useEffect(() => {
    const loadPlan = async () => {
      try {
        setLoading(true);
        setError(null);

        const data =
          await careerApi.getDailyPlan();

        setDailyPlan(data);

      } catch (err: any) {
        console.error(
          'Daily plan error:',
          err
        );

        setError(
          err?.response?.data?.message ||
            'Failed to load daily preparation plan'
        );

      } finally {
        setLoading(false);
      }
    };

    loadPlan();
  }, []);


  // =====================================================
  // TOGGLE TASK
  // =====================================================

  const handleToggle = async (
    taskId: number
  ) => {
    try {
      setTogglingId(taskId);
      setError(null);

      const updatedPlan =
        await careerApi.toggleDailyTask(
          taskId
        );

      setDailyPlan(updatedPlan);

    } catch (err: any) {
      console.error(
        'Toggle task error:',
        err
      );

      setError(
        err?.response?.data?.message ||
          'Failed to update task status'
      );

    } finally {
      setTogglingId(null);
    }
  };


  // =====================================================
  // OPEN TASK
  // =====================================================

  const handleOpenTask = (
    actionLink: string
  ) => {

    console.log(
      'Opening daily plan route:',
      actionLink
    );

    if (!actionLink) {
      console.error(
        'No actionLink found for task'
      );
      return;
    }

    navigate(actionLink);
  };


  // =====================================================
  // CATEGORY ICON
  // =====================================================

  const getCategoryIcon = (
    category: string
  ) => {

    switch (category) {

      case 'JAVA_TOPIC':
        return (
          <BookOpen
            className="h-4 w-4 text-blue-600"
          />
        );

      case 'MCQ_PRACTICE':
        return (
          <Brain
            className="h-4 w-4 text-emerald-600"
          />
        );

      case 'DSA_PROBLEM':
        return (
          <Code2
            className="h-4 w-4 text-purple-600"
          />
        );

      case 'SQL_PRACTICE':
        return (
          <FileText
            className="h-4 w-4 text-amber-600"
          />
        );

      case 'INTERVIEW_QA':
        return (
          <FileCheck
            className="h-4 w-4 text-indigo-600"
          />
        );

      case 'MOCK_INTERVIEW':
        return (
          <ShieldCheck
            className="h-4 w-4 text-violet-600"
          />
        );

      case 'JOB_APPLY':
        return (
          <FileText
            className="h-4 w-4 text-green-600"
          />
        );

      default:
        return (
          <Sparkles
            className="h-4 w-4 text-slate-600"
          />
        );
    }
  };


  // =====================================================
  // LOADING
  // =====================================================

  if (loading) {
    return (
      <div className="flex items-center justify-center py-24">

        <div
          className="
            h-8
            w-8
            animate-spin
            rounded-full
            border-4
            border-indigo-600
            border-t-transparent
          "
        />

      </div>
    );
  }


  // =====================================================
  // ERROR
  // =====================================================

  if (error || !dailyPlan) {
    return (
      <div
        className="
          rounded-xl
          border
          border-red-200
          bg-red-50
          p-6
          text-center
          text-red-700
        "
      >

        <AlertCircle
          className="mx-auto mb-2 h-8 w-8"
        />

        <p className="font-semibold">
          {error ||
            'Daily preparation plan unavailable.'}
        </p>

      </div>
    );
  }


  // =====================================================
  // MAIN PAGE
  // =====================================================

  return (
    <div
      className="
        mx-auto
        max-w-4xl
        space-y-8
        pb-12
      "
    >

      {/* =================================================
          HEADER
      ================================================= */}

      <div
        className="
          rounded-2xl
          bg-gradient-to-r
          from-slate-900
          via-indigo-950
          to-slate-900
          p-8
          text-white
          shadow-xl
        "
      >

        <div
          className="
            flex
            flex-col
            gap-6
            md:flex-row
            md:items-center
            md:justify-between
          "
        >

          {/* HEADER LEFT */}

          <div className="space-y-1.5">

            <div
              className="
                inline-flex
                items-center
                gap-2
                rounded-full
                border
                border-indigo-400/30
                bg-indigo-500/20
                px-3
                py-1
                text-xs
                font-semibold
                text-indigo-300
              "
            >

              <Calendar
                className="h-3.5 w-3.5"
              />

              Target Routine:
              {' '}
              {dailyPlan.planDate}

            </div>


            <h1
              className="
                text-2xl
                font-bold
                tracking-tight
                md:text-3xl
              "
            >
              Daily High-Yield Preparation Plan
            </h1>


            <p
              className="
                max-w-xl
                text-xs
                text-slate-300
                md:text-sm
              "
            >
              Consistent daily practice across
              MCQ quizzes, DSA coding patterns,
              ATS resume audits, and mock rounds
              ensures placement readiness.
            </p>

          </div>


          {/* PROGRESS */}

          <div
            className="
              min-w-[160px]
              rounded-2xl
              border
              border-white/10
              bg-white/10
              p-5
              text-center
              backdrop-blur-md
            "
          >

            <p
              className="
                text-xs
                font-semibold
                uppercase
                tracking-wider
                text-slate-300
              "
            >
              Today's Progress
            </p>


            <p
              className="
                mt-1
                text-3xl
                font-bold
                text-white
              "
            >
              {dailyPlan.completedTasks}
              {' / '}
              {dailyPlan.totalTasks}
            </p>


            <div
              className="
                mt-2
                h-1.5
                w-full
                overflow-hidden
                rounded-full
                bg-white/20
              "
            >

              <div
                className="
                  h-full
                  rounded-full
                  bg-emerald-400
                  transition-all
                  duration-500
                "
                style={{
                  width:
                    `${dailyPlan.completionPercentage}%`,
                }}
              />

            </div>


            <p
              className="
                mt-1
                text-[11px]
                font-medium
                text-emerald-300
              "
            >
              {dailyPlan.completionPercentage}%
              {' '}
              Completed
            </p>

          </div>

        </div>

      </div>


      {/* =================================================
          TASKS
      ================================================= */}

      <div className="space-y-4">

        <h2
          className="
            flex
            items-center
            gap-2
            text-base
            font-bold
            text-slate-900
          "
        >

          <TrendingUp
            className="h-4 w-4 text-indigo-600"
          />

          Focus Tasks for Today

        </h2>


        <div className="space-y-3">

          {dailyPlan.items
            .slice()
            .sort(
              (
                a: DailyPlan['items'][number],
                b: DailyPlan['items'][number]
              ) =>
                a.displayOrder -
                b.displayOrder
            )
            .map(
              (
                item: DailyPlan['items'][number],
                index: number
              ) => (

                <div
                  key={item.id}
                  className={`
                    flex
                    flex-col
                    justify-between
                    gap-4
                    rounded-xl
                    border
                    p-4
                    transition-all
                    sm:flex-row
                    sm:items-center

                    ${
                      item.isCompleted
                        ? `
                          border-emerald-200
                          bg-emerald-50/40
                          text-slate-600
                        `
                        : `
                          border-slate-200
                          bg-white
                          text-slate-800
                          hover:border-indigo-300
                          hover:shadow-sm
                        `
                    }
                  `}
                >

                  {/* LEFT SIDE */}

                  <div
                    className="
                      flex
                      items-start
                      gap-3.5
                    "
                  >

                    {/* CHECKBOX */}

                    <button
                      type="button"
                      onClick={() =>
                        handleToggle(
                          item.id
                        )
                      }
                      disabled={
                        togglingId ===
                        item.id
                      }
                      className={`
                        mt-0.5
                        flex-shrink-0
                        transition-transform
                        active:scale-90

                        ${
                          item.isCompleted
                            ? `
                              text-emerald-600
                            `
                            : `
                              text-slate-400
                              hover:text-indigo-600
                            `
                        }
                      `}
                    >

                      {item.isCompleted ? (

                        <CheckCircle
                          className="h-5 w-5"
                        />

                      ) : (

                        <Circle
                          className="h-5 w-5"
                        />

                      )}

                    </button>


                    {/* TASK INFORMATION */}

                    <div className="space-y-1">

                      <div
                        className="
                          flex
                          items-center
                          gap-2
                        "
                      >

                        <span
                          className="
                            rounded-md
                            bg-slate-100
                            p-1
                          "
                        >
                          {getCategoryIcon(
                            item.category
                          )}
                        </span>


                        <span
                          className={`
                            text-sm
                            font-semibold

                            ${
                              item.isCompleted
                                ? `
                                  text-slate-500
                                  line-through
                                `
                                : `
                                  text-slate-900
                                `
                            }
                          `}
                        >
                          {index + 1}.
                          {' '}
                          {item.title}
                        </span>

                      </div>


                      <p
                        className="
                          max-w-xl
                          text-xs
                          leading-relaxed
                          text-slate-500
                        "
                      >
                        Complete the required
                        target for this task.
                      </p>

                    </div>

                  </div>


                  {/* RIGHT SIDE */}

                  <div
                    className="
                      flex
                      items-center
                      justify-between
                      gap-3
                      pl-8
                      sm:justify-end
                      sm:pl-0
                    "
                  >

                    {/* TARGET */}

                    <span
                      className="
                        font-mono
                        text-[11px]
                        font-medium
                        text-slate-400
                      "
                    >
                      Target:
                      {' '}
                      {item.completedCount}
                      /
                      {item.targetCount}
                    </span>


                    {/* OPEN BUTTON */}

                    <button
                      type="button"
                      onClick={() =>
                        handleOpenTask(
                          item.actionLink
                        )
                      }
                      className={`
                        inline-flex
                        items-center
                        gap-1.5
                        rounded-xl
                        px-3.5
                        py-1.5
                        text-xs
                        font-semibold
                        shadow-sm
                        transition-colors

                        ${
                          item.isCompleted
                            ? `
                              bg-slate-100
                              text-slate-600
                              hover:bg-slate-200
                            `
                            : `
                              bg-indigo-600
                              text-white
                              hover:bg-indigo-500
                            `
                        }
                      `}
                    >

                      Open

                      <ArrowRight
                        className="h-3 w-3"
                      />

                    </button>

                  </div>

                </div>

              )
            )}

        </div>

      </div>

    </div>
  );
}