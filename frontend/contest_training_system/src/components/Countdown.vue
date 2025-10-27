<template>
  <span class="countdown">{{ displayTime }}</span>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue';

interface Props {
  endTime: string;
}

const props = defineProps<Props>();
const emit = defineEmits<{
  finish: [];
}>();

const remainingSeconds = ref(0);
const timer = ref<ReturnType<typeof setInterval> | null>(null);

const displayTime = computed(() => {
  if (remainingSeconds.value <= 0) {
    return '00:00:00';
  }

  const days = Math.floor(remainingSeconds.value / 86400);
  const hours = Math.floor((remainingSeconds.value % 86400) / 3600);
  const minutes = Math.floor((remainingSeconds.value % 3600) / 60);
  const seconds = remainingSeconds.value % 60;

  if (days > 0) {
    return `${days}天 ${padZero(hours)}:${padZero(minutes)}:${padZero(seconds)}`;
  }

  return `${padZero(hours)}:${padZero(minutes)}:${padZero(seconds)}`;
});

const padZero = (num: number) => {
  return num.toString().padStart(2, '0');
};

const calculateRemaining = () => {
  const end = new Date(props.endTime.replace(' ', 'T')).getTime();
  const now = Date.now();
  const diff = Math.floor((end - now) / 1000);

  if (diff <= 0) {
    remainingSeconds.value = 0;
    if (timer.value) {
      clearInterval(timer.value);
      timer.value = null;
    }
    emit('finish');
  } else {
    remainingSeconds.value = diff;
  }
};

onMounted(() => {
  calculateRemaining();
  timer.value = setInterval(calculateRemaining, 1000);
});

onUnmounted(() => {
  if (timer.value) {
    clearInterval(timer.value);
  }
});
</script>

<style scoped>
.countdown {
  font-variant-numeric: tabular-nums;
  font-feature-settings: 'tnum';
}
</style>

