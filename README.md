# AiHelpMeGetJob

## 🇬🇧 [English](README.md) / 🇺🇸 [中文](README-zh-CN.md)

## 🇺🇸 English Documentation

### Project Overview
This is an Android application based on accessibility services, intelligently analyzes job descriptions and requirements for job seekers, matches positions, and conducts initial communications.

### Core Features
- **Accessibility Parsing**: Read Boss Zhiping app interface using Android AccessibilityService
- **AI Analysis**: LLM Agent intelligently analyzes job match scores and conducts preliminary communication


### Usage Steps
   - Open this application, set relevant parameters including job-related information, personal resume information, and LLM API KEY/URL information
   - Start task from home page, the app will detect if accessibility mode is enabled, if not enabled, it will guide to accessibility mode enable interface, only after enabling can start the task
   - After starting the task, automatically start Boss Zhiping application, please manually click on job entries, entering the job entry will intelligently analyze and combine job requirements and skills for AI scoring of job match


### Important Notes
- ⚠️ Must run in accessibility mode
- ⚠️ Be sure to set all parameters correctly before starting tasks to avoid illegal scenarios
- ⚠️ LLM API provides test button, be sure to fill in and test to ensure API is available
- ⚠️ This application is only for learning and exchange purposes, please do not use for illegal purposes
