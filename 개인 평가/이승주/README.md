### ui 레퍼런스

[public.tableau.com](https://public.tableau.com/app/profile/rebecca.merton/viz/GlobalImmigrationDetention/Story1)

[CX Data Trend｜Beusable](https://www.beusable.net/ko/cxdata/industries)

### Vue 코드 React로 변환

- Vue

```jsx
<template>
    <div
        class="fixed inset-0 z-50 flex items-center justify-center overflow-auto bg-black bg-opacity-50"
    >
        <!-- 전체 컨테이너: 가운데 정렬, 배경색 적용, 너비 지정 -->
        <div
            class="flex flex-col items-center bg-white w-[1054px] h-fit gap-6 p-6 border border-gray-200 rounded-[20px]"
        >
            <!-- 상단 강의 추가 섹션 -->
            <div class="flex items-center justify-between w-full text-black text-h3">
                <p>어떤 강의를 추가하시겠어요?</p>
                <div class="flex items-center gap-2">
                    <div
                        :class="[
                            isButtonClicked
                                ? 'cursor-pointer button-primary'
                                : 'cursor-pointer button-line',
                        ]"
                        @click="(submitTodo(token, userId), $emit('close'))"
                    >
                        할 일 추가
                    </div>
                    <Delete class="w-6 h-6 bg-white cursor-pointer" @click="$emit('close')" />
                </div>
            </div>
            <!-- 날짜 선택 및 강의 목록 컨테이너 -->
            <div class="flex flex-col gap-y-2.5 w-[58.25rem]">
                <div class="relative w-fit">
                    <div
                        class="flex items-center border border-gray-200 w-full h-9 gap-x-2 px-[0.75rem] rounded cursor-pointer"
                        @click="toggleCalendarDropdown"
                    >
                        <p class="text-body-bold">{{ formattedDate }}</p>
                        <NavigateDown class="w-5 h-5" />
                    </div>

                    <div
                        class="absolute left-0 z-50 top-full w-fit min-w-[450px] transform: scale(0.10)"
                    >
                        <TodoAddModalCalendar
                            v-if="isCalendarDropdownOpen"
                            @select-date="selectDate"
                            @click-outside="closeCalendarDropdown"
                            class="bg-white border border-gray-200 rounded-lg shadow-lg"
                        />
                    </div>
                </div>
                <!-- 강의 선택 및 선택된 강의 컨테이너 -->
                <div
                    class="flex w-full h-fit rounded-[20px] overflow-hidden bg-gray-100 border border-gray-200"
                >
                    <div class="w-[29.125rem] h-[240.8px] overflow-y-auto">
                        <!-- 나중에 :class에서 siteName대신 id로 바꾸기-->
                        <div
                            v-for="lectureData in todoStore.inprogressLectures"
                            :key="lectureData.id"
                            class="flex flex-col h-auto gap-1 px-4 py-3 border-b border-r border-gray-200 hover:bg-primary-100"
                            :class="{
                                'bg-primary-100': selectedLectureId === lectureData.lecture.id,
                                'bg-white': selectedLectureId !== lectureData.lecture.id,
                            }"
                            @click="selectLecture(lectureData)"
                        >
                            <p class="text-gray-300 text-caption-sm">
                                {{ lectureData.lecture.sourceName }}
                            </p>
                            <p
                                class="overflow-hidden text-black cursor-pointer text-body text-ellipsis whitespace-nowrap"
                                :title="lectureData.lecture.name"
                            >
                                {{ lectureData.lecture.name }}
                            </p>
                        </div>
                    </div>
                    <!-- 선택된 강의 목록 (오른쪽 영역) -->
                    <div class="w-[29.125rem] h-[240.8px] overflow-y-auto">
                        <div
                            v-for="(subLecture, index) in filteredSubLectures"
                            :key="index"
                            class="flex flex-col gap-1 px-4 py-3 border-b border-gray-200 hover:bg-primary-100"
                            :class="{
                                'bg-primary-100': subLectureId === index,
                                'bg-white': subLectureId !== index,
                            }"
                            @click="selectsubLecture(subLecture, index)"
                        >
                            <p class="text-gray-300 text-caption-sm">{{ index + 1 }}강</p>
                            <p
                                class="overflow-hidden text-black cursor-pointer text-body text-ellipsis whitespace-nowrap selectLecture"
                            >
                                {{ subLecture.title }}
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import TodoAddModalCalendar from './TodoAddModalCalendar.vue'
import Delete from '@/assets/icons/delete.svg'
import NavigateDown from '@/assets/icons/navigate_down.svg'
import { ref, computed, watch } from 'vue'
import { useTodoStore } from '@/stores/todo'
import { useUserStore } from '@/stores/user'
import { useRoute } from 'vue-router'
import { getTodos } from '@/helpers/todo'

defineProps({
    userId: {
        type: String,
        default: '',
    },
    token: {
        type: String,
        default: '',
    },
})
const route = useRoute()
const userStore = useUserStore()
const todoStore = useTodoStore() // Pinia 스토어 가져오기

// 강의 추가 버튼 상태 관리
const isButtonClicked = ref(false)

// 선택한 날짜 포맷팅 (Store의 selectedDate 사용)
const formattedDate = computed(() => {
    if (!todoStore.selectedDate) return ''
    const date = todoStore.selectedDate
    return `${date.getMonth() + 1}월 ${date.getDate()}일` // 보기 좋은 형식으로 변환
})

// 날짜 선택 시 Store의 selectedDate 업데이트
const selectDate = (date) => {
    todoStore.updateSelectedDate(date)
    isCalendarDropdownOpen.value = false
}

// 캘린더 드롭다운 상태 관리
const isCalendarDropdownOpen = ref(false) // 드롭다운 상태

// 드롭다운 열기/닫기 함수
const toggleCalendarDropdown = () => {
    isCalendarDropdownOpen.value = !isCalendarDropdownOpen.value
}

// 대강의 목록(mount될 때 저장)
const lectures = computed(() => todoStore.inprogressLectures)

// 선택한 대강의 ID, 이름, url
const selectedLectureId = ref(null)
const selectedLectureName = ref(null)
const selectedLectureURL = ref(null)

// 선택한 subLecture 목록 (배열로 관리)
const selectedSubLectures = ref(null)

// 선택한 subLecture ID, 이름
const subLectureId = ref(null)
const subLectureName = ref(null)

// 대강의 선택 / id와 이름 저장
const selectLecture = (lecture) => {
    selectedLectureId.value = lecture.lecture.id
    selectedLectureName.value = lecture.lecture.name
    selectedLectureURL.value = lecture.lecture.sourceUrl
    selectedSubLectures.value = null // 대강의 변경 시 subLecture 초기화
    subLectureId.value = null // ✅ 선택된 subLecture도 초기화!
}

// 선택한 대강의에 해당하는 subLectures 가져오기 (computed 활용),
// selectedLectureId에 해당하는 subLectures를 찾아 반환
// selectedLectureId, lecture 데이터가 바뀌면 자동으로 그 강의의 subLectures를 업데이트
const filteredSubLectures = computed(() => {
    // selectedLectureId와 lecture.lecture.id를 비교
    const selectedLecture = lectures.value.find(
        (lecture) => lecture.lecture.id === selectedLectureId.value
    )

    if (!selectedLecture || !selectedLecture.lecture.curriculum) return []

    // curriculum 객체를 배열로 변환 후 subLectures 배열 가져오기
    return Object.values(selectedLecture.lecture.curriculum).flatMap(
        (curriculumItem) => curriculumItem.subLectures
    )
})

// subLecture 선택
const selectsubLecture = (subLecture, index) => {
    subLectureId.value = index //subLecture의 고유 ID 저장
    subLectureName.value = subLecture.title
    isButtonClicked.value = true
}

// Todo 추가 요청
const submitTodo = async () => {
    const todoData = {
        lectureId: selectedLectureId.value,
        lectureName: selectedLectureName.value,
        subLectureName: subLectureName.value,
        sourceUrl: selectedLectureURL.value,
        date: todoStore.selectedDate.toISOString().split('T')[0],
        finished: false,
    }

    try {
        await todoStore.addTodo(todoData, userStore.token, route.params.id)
        // console.log('나와라 토큰', token)

        const formattedDate = todoStore.selectedDate.toISOString().split('T')[0]
        const response = await getTodos(userStore.token, route.params.id, formattedDate)
        // console.log(response.data)

        todoStore.todos = response.data // store에 Todo 리스트 저장
        selectedLectureId.value = null
        subLectureId.value = null
        // alert('할 일이 추가되었습니다!')
        isButtonClicked.value = !isButtonClicked.value
    } catch (error) {
        console.error('🚨 할 일 추가 실패:', error)
    }
}

//  `filteredSubLectures`가 변경될 때 자동으로 `selectedSubLectures` 업데이트
watch(
    () => [filteredSubLectures.value, userStore.token, userStore.userId, todoStore.todos], 
    async ([newSubLectures]) => {
        if (newSubLectures) {
            //  filteredSubLectures가 변경되었을 때
            selectedSubLectures.value = newSubLectures
        }
    },
    { immediate: true }
)

// onMounted(() => {
//     todoStore.getInprogressLecture() // 컴포넌트가 로드될 때 JSON 데이터 가져오기
// })
</script>

<style></style>

```

- React

```jsx
import { useState, useEffect, useMemo } from "react";
import TodoAddModalCalendar from "./TodoAddModalCalendar";
import DeleteIcon from "@/assets/icons/delete.svg";
import NavigateDownIcon from "@/assets/icons/navigate_down.svg";
import { useTodoStore } from "@/stores/todo"; // Pinia 대신 zustand, Redux, Context API 활용 가능
import { useUserStore } from "@/stores/user";
import { useParams } from "react-router-dom";
import { getTodos } from "@/helpers/todo";

function TodoAddModal({userId, token, onClose}) {
    const {id} = useParams();
    const userStore = userUserStore();
    const todoStore = useTodoStore(); 

    // 버튼 상태 관리
    const [isButtonClicked,setIsbuttonClicked] = useState(false);

    // 캘린더 드롭다운 상태 관리
    const [isCalendarDropdownOpen, setIsCalendarDropdownOpen] = useState(false)

    // 선택된 강의 및 강의 목록 관리
    const [selectedSubLectures, setSelectedSubLectures] = useState(null);
    const [subLectureId, setSubLectureId] = useState(null);
    const [subLectureName, setSubLectureName] = useState(null);

    // 날짜 선택
    const formattedDate = useMemo(() => {
        if (!todoStore.selectedDate) return ""
        const date = todoStore.selectedDate
        return `${date.getMonth() +1}월 ${date.getDate()}일`;
    }, [todoStore.selectedDate])

    const selectDate = (date) => {
        todoStore.updateSelectedDate(date);
        setIsCalendarDropdownOpen(false);
    }

    // 드롭다운 토글
    const toggleCalendarDropdown = () => {
        setIsCalendarDropdownOpen(!isCalendarDropdownOpen);
    }

    // 진행중인 강의 목록
    const lectures = useMemo(() => todoStore.inprogressLectures, [userStore.lectures])

    useEffect(() => {
        if (filteredSubLectures) {
            setSelectedSubLectures(filteredSubLectures)
        }
    }, [lectures])

    return (
        <div></div>
    )
}
```

<aside>

## useState

```jsx
import { useState } from "react";

function Example() {
  const [isButtonClicked, setIsButtonClicked] = useState(false);

  return (
    <button onClick={() => setIsButtonClicked(!isButtonClicked)}>
      {isButtonClicked ? "클릭됨" : "클릭하세요"}
    </button>
  );
}

```

### 🔹 실행 흐름

1. `isButtonClicked`의 초기값은 `false`.
2. 버튼을 클릭하면 `setIsButtonClicked(!isButtonClicked)` 실행 → `true`로 변경.
3. 다시 클릭하면 `false`로 변경.
4. 버튼 내부 텍스트가 `"클릭됨"` / `"클릭하세요"`로 바뀜.

---

## **`isButtonClicked, setIsButtonClicked`의 역할**

```jsx

const [isButtonClicked, setIsButtonClicked] = useState(false);
```

✔ `isButtonClicked` → **버튼이 클릭되었는지 여부를 나타내는 상태 변수**

✔ `setIsButtonClicked` → **상태를 변경하는 함수**

`setIsButtonClicked(true)`를 호출하면 `isButtonClicked`가 `true`로 바뀜.

</aside>